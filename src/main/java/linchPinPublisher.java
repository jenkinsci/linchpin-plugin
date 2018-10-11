import hudson.*;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Aviel
 */
public class linchPinPublisher extends Publisher {
    private String installation;

    @DataBoundConstructor
    public linchPinPublisher() {}

    public String getInstallation() {
        return installation;
    }

    @DataBoundSetter
    public void setInstallation(String installation) {
        this.installation = Util.fixEmpty(installation);
    }
    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        String pathToCurrentInstallation = readTmp();
        if(pathToCurrentInstallation == null){
            throw new AbortException("Can't find /tmp/linchpin.out");
        }
        toCmd(build.getWorkspace()+"/venv", "bin/linchpin destroy",launcher,listener);
        toCmd("","rm /tmp/linchpin.out",launcher,listener);
        return true;
    }

    public String readTmp() throws IOException,InterruptedException{
        FilePath fileName = new FilePath(new File("/tmp/linchpin.out"));
        if(!fileName.exists()) return null;
        BufferedReader br = new BufferedReader(new FileReader("/tmp/linchpin.out"));
        return br.readLine();
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

    @Extension
    public static class DescriptorImp extends BuildStepDescriptor<Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return "LinchPin TearDown";
        }
    }
}
