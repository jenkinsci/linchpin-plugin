import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.IOException;

/**
 * @author Aviel
 */
public class linchPinBuilder extends Builder {
    private String areaToConfig;
    @DataBoundConstructor
    public linchPinBuilder(String s){ this.areaToConfig = s;}

    public String getAreaToConfig() {
        return areaToConfig;
    }

    @DataBoundSetter
    public void setAreaToConfig(String areaToConfig) {
        this.areaToConfig = areaToConfig;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        //TODO configuration
        listener.getLogger().println("TODO perform configuration "+ getAreaToConfig());
        
        return true;
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public String getDisplayName() { return "Invoke linchPin"; }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) { return true; }
    }
}
