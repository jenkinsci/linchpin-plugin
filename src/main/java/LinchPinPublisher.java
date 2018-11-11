import hudson.*;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.util.ArgumentListBuilder;
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
public class LinchPinPublisher extends Publisher implements SimpleBuildStep {
    private String inventory;
    private String targets;
    private boolean verbose;

    @DataBoundConstructor
    public LinchPinPublisher() { }

    public String getInventory() {
        return inventory;
    }

    @DataBoundSetter
    public void setInventory(String inventory) { this.inventory = Util.fixEmpty(inventory); }

    public String getTargets() { return targets; }

    @DataBoundSetter
    public void setTargets(String targets) { this.targets = Util.fixEmpty(targets); }

    public boolean isVerbose() { return verbose; }

    @DataBoundSetter
    public void setVerbose(boolean verbose) { this.verbose = verbose; }


    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws InterruptedException, IOException {
        LinchPinUtil util = new LinchPinUtil();
        if(util.readTmp() == null){
            throw new AbortException("You might want to use LinchPin first.");
        }

        if(inventory != null){
            util.toCmd(workspace + "", "bin/teardown "+inventory,launcher,listener);
        }

        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add("bin/linchpin");

        if (verbose == true) args.add("-v");

        args.add("destroy");

        if(targets != null){
            String[] targetsArr = targets.split(",");
            for (String s: targetsArr){
                args.add(s.trim());
            }
        }
        util.toCmd(workspace + "",args,launcher,listener);

        util.toCmd("","rm /tmp/linchpin.out",launcher,listener);
    }


    @Extension @Symbol("linchPinTearDown")
    public static class DescriptorImp extends BuildStepDescriptor<Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return "LinchPin Teardown";
        }
    }
}
