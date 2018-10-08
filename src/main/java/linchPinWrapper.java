import Installation.linchPinTool;
import hudson.*;
import hudson.model.*;
import hudson.slaves.NodeSpecific;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.tools.ToolInstallation;
import hudson.util.ArgumentListBuilder;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildWrapper;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class linchPinWrapper extends SimpleBuildWrapper {
    private String installation, pinfile,layoutFile,topologyFile,layoutFileName,topologyFileName;
    private String installationHome = null;

    @DataBoundConstructor
    public linchPinWrapper() {}

    @DataBoundSetter
    public void setPinFile(String file){
        this.pinfile = Util.fixEmpty(file);
    }

    @DataBoundSetter
    public void setLayoutFile(String file){
        this.layoutFile = Util.fixEmpty(file);
    }

    @DataBoundSetter
    public void setTopologyFile(String file){
        this.topologyFile = Util.fixEmpty(file);
    }

    @DataBoundSetter
    public void setLayoutFileName(String name){
        this.layoutFileName = Util.fixEmpty(name);
    }

    @DataBoundSetter
    public void setTopologyFileName(String name){
        this.topologyFileName = Util.fixEmpty(name);
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
        installIfNecessary(context,workspace,listener,initialEnvironment);

        createFile(layoutFile,"/venv/layouts/",layoutFileName);
        createFile(topologyFile,"/venv/topologies/",topologyFileName);
        modifyFile(pinfile);

        toCmd(installationHome+"/venv","bin/linchpin up",launcher,listener);
    }

    /**
     * Create files for linchpin configuration
     * @param file
     * @param path
     * @param name
     * @throws IOException
     */
    private void createFile(String file, String path, String name) throws IOException{
        if(file == null) return;
        if(name == null) name = "defaultName.yml";
        String fileName = installationHome+path+name;
        if(!fileName.endsWith(".yml")) fileName+=".yml";
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(file+"\n");
        writer.close();
    }

    /**
     * Help method to launch commands to cmd
     * @param pwd - the dir that the command is running in
     * @param command - the command itself
     * @param launcher
     * @param listener
     * @throws IOException
     * @throws InterruptedException
     */
    private void toCmd(String pwd,String command,Launcher launcher, TaskListener listener)
            throws IOException, InterruptedException{
        Launcher.ProcStarter starter = launcher.launch().cmds(command.split(" "));
        int exit = starter.pwd(pwd).stdout(listener).join();
        if(exit!=0) listener.getLogger().println("Exit code is " + exit);
    }

    /**
     * Modify File - if empty - use default
     * @param pinFile
     * @throws IOException
     */
    private void modifyFile(String pinFile) throws IOException{
        if(pinFile == null) return;
        BufferedWriter writer = new BufferedWriter(new FileWriter(installationHome+"/venv/PinFile"));
        writer.write(pinFile+"\n");
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
    private void installIfNecessary(Context context,FilePath workspace, TaskListener listener, EnvVars initialEnvironment)
            throws IOException, InterruptedException{
        ToolInstallation[] tools = Jenkins.getActiveInstance().getDescriptorByType(linchPinTool.DescriptorImpl.class).getInstallations();
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
        }
        installationHome = inst.getHome();
    }
    @Extension
    public static class DescriptorImpl extends BuildWrapperDescriptor{

        public DescriptorImpl() {
            super(linchPinWrapper.class);
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
