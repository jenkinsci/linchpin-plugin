import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import jenkins.tasks.SimpleBuildStep;
import org.kohsuke.stapler.DataBoundConstructor;
import util.linchPinUtil;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Aviel
 */
public class linchPinBuilder extends Builder implements SimpleBuildStep {
    @DataBoundConstructor
    public linchPinBuilder() { }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener listener)
            throws InterruptedException, IOException {}

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        listener.getLogger().println("--------------Second perform----------------");
        linchPinUtil util = new linchPinUtil();
        util.toCmd(build.getWorkspace()+"","bin/linchpin up",launcher,listener);
        listener.getLogger().println("--------------Second perform----------------");
        return true;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        @Override
        public String getDisplayName() { return "LinchPin Up";}

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) { return true; }
    }
}
