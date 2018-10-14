import hudson.*;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import util.linchPinUtil;

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
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
        linchPinUtil util = new linchPinUtil();
        if(util.readTmp() == null){
            throw new AbortException("You might want to use LinchPin first.");
        }

        util.toCmd(build.getWorkspace() + "", "bin/linchpin destroy",launcher,listener);
        util.toCmd("","rm /tmp/linchpin.out",launcher,listener);
        return true;
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
