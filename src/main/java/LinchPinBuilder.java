import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import util.LinchPinUtil;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Aviel
 */
public class LinchPinBuilder extends Builder implements SimpleBuildStep {
    private String inventory;

    @DataBoundConstructor
    public LinchPinBuilder() { }

    public String getInventory() {
        return inventory;
    }

    @DataBoundSetter
    public void setInventory(String inventory) { this.inventory = Util.fixEmpty(inventory); }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener listener)
            throws InterruptedException, IOException {
        LinchPinUtil util = new LinchPinUtil();
        String workspace = run.getEnvironment(listener).get("WORKSPACE");

        util.toCmd(workspace,"bin/linchpin up",launcher,listener);

        if(inventory != null){
            util.toCmd(workspace, "bin/cinch "+inventory,launcher,listener);
        }
    }

    @Extension @Symbol("linchPinUp")
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        @Override
        public String getDisplayName() { return "LinchPin Up";}

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) { return true; }
    }
}
