import Installation.LinchPinTool;
import hudson.*;
import hudson.model.*;
import hudson.slaves.NodeSpecific;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.tools.ToolInstallation;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildWrapper;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import util.LinchPinUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author Aviel
 */
public class LinchPinWrapper extends SimpleBuildWrapper {
    private String installation, pinFile;

    @DataBoundConstructor
    public LinchPinWrapper() {}

    @DataBoundSetter
    public void setPinFile(String file){
        this.pinFile = Util.fixEmpty(file);
    }

    public String getInstallation() {
        return installation;
    }

    @DataBoundSetter
    public void setInstallation(String installation) {
        this.installation = Util.fixEmpty(installation);
    }

    @Override
    public void setUp(Context context, Run<?, ?> build, FilePath workspace, Launcher launcher, TaskListener listener, EnvVars initialEnvironment)
            throws IOException, InterruptedException {
        LinchPinUtil util = new LinchPinUtil();
        installIfNecessary(context,workspace,listener,initialEnvironment,launcher,util);

        modifyPinFile(pinFile,workspace+"");

        String pathToPrevInstallation = util.readTmp();
        if(pathToPrevInstallation != null && new FilePath(new File(pathToPrevInstallation)).exists())
            util.tearDownPrevLinchPin(pathToPrevInstallation,launcher,listener,context);

        util.createFile(workspace + "","/tmp/","linchpin.out");
    }

    /**
     * initiate linchpin on workspace
     * @param context
     * @param util
     * @param workspace
     * @param launcher
     * @param listener
     * @throws IOException
     * @throws InterruptedException
     */
    private void linchPinInit(Context context, LinchPinUtil util, FilePath workspace, Launcher launcher, TaskListener listener)
            throws IOException,InterruptedException{
        String linchPinHome = context.getEnv().get("LINCHPIN_HOME");
        FilePath venv = new FilePath(new File(linchPinHome));
        for (int i = 0; i < venv.list().size(); ++i){
            util.toCmd(workspace + "","ln -s "+venv.list().get(i)+" "+workspace,launcher,listener,context);
        }
        util.toCmd(workspace + "", "bin/linchpin init",launcher,listener,context);
    }

    /**
     * Modify PinFile - if empty - use default
     * @param content
     * @param path
     * @throws IOException
     */
    private void modifyPinFile(String content,String path) throws IOException{
        if(content == null) return;
        BufferedWriter writer = new BufferedWriter
                (new OutputStreamWriter(new FileOutputStream(path+"/PinFile"), StandardCharsets.UTF_8));
        writer.write(content+"\n");
        writer.close();
    }

    /**
     * Help method to install the tool if necessary
     * @param context
     * @param workspace
     * @param listener
     * @param initialEnvironment
     * @throws IOException
     * @throws InterruptedException
     */
    private void installIfNecessary(Context context, FilePath workspace, TaskListener listener, EnvVars initialEnvironment, Launcher launcher, LinchPinUtil util)
            throws IOException, InterruptedException{
        ToolInstallation[] tools = Jenkins.getActiveInstance().getDescriptorByType(LinchPinTool.DescriptorImpl.class).getInstallations();
        ToolInstallation inst = null;
        for (ToolInstallation _inst : tools) {
            if (_inst.getName().equals(installation)) {
                inst = _inst;
                break;
            }
        }
        if(installation != null){
            if (inst == null) {
                throw new AbortException("no such tool ‘" + installation + "’");
            }
            if (inst instanceof NodeSpecific) {
                Computer computer = workspace.toComputer();
                if (computer != null) {
                    Node node = computer.getNode();
                    if (node != null) {
                        inst = (ToolInstallation) ((NodeSpecific) inst).forNode(node, listener);
                    }
                }
            }
            if (inst instanceof EnvironmentSpecific) {
                inst = (ToolInstallation) ((EnvironmentSpecific) inst).forEnvironment(initialEnvironment);
            }
            EnvVars modified = new EnvVars();
            inst.buildEnvVars(modified);
            for (Map.Entry<String, String> entry : modified.entrySet()) {
                context.env(entry.getKey(), entry.getValue());
            }

            if(!new FilePath(new File(workspace+"/PinFile")).exists())
                linchPinInit(context,util,workspace,launcher,listener);
        }
    }
    @Extension @Symbol("withLinchPin")
    public static class DescriptorImpl extends BuildWrapperDescriptor{

        public DescriptorImpl() {
            super(LinchPinWrapper.class);
        }

        @Override
        public String getDisplayName() {
            return "Use LinchPin";
        }

        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }
    }
}
