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
    private String installation, pinfile;
    private String installationHome = null;

    @DataBoundConstructor
    public linchPinWrapper() {}

    @DataBoundSetter
    public void setPinfile(String pinfile){
        this.pinfile = Util.fixEmpty(pinfile);
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
        modifyPinFile(pinfile,listener);
        toCmd(installationHome+"/venv","bin/linchpin up",launcher,listener);
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
     * Modify PinFile - if PinFile inserted by the user is empty - use default
     * @param pinFile
     * @param listener
     * @throws IOException
     */
    private void modifyPinFile(String pinFile,TaskListener listener) throws IOException{
        if(pinFile == null) {
            listener.getLogger().println("Using default PinFile");
            return;
        }
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
