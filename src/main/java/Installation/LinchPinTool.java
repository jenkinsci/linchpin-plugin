package Installation;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.EnvVars;
import hudson.Extension;
import hudson.model.EnvironmentSpecific;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.slaves.NodeSpecific;
import hudson.tools.ToolDescriptor;
import hudson.tools.ToolInstallation;
import hudson.tools.ToolProperty;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.List;

/**
 * @author Aviel
 */
public class LinchPinTool extends ToolInstallation
        implements NodeSpecific<LinchPinTool>, EnvironmentSpecific<LinchPinTool> {

    @DataBoundConstructor
    public LinchPinTool(String name, String home, List<? extends ToolProperty<?>> properties) {
        super(name, home, properties);
    }

    @Override
    public LinchPinTool forEnvironment(EnvVars envVars) {
        return new LinchPinTool(getName(),envVars.expand(getHome()),getProperties().toList());
    }

    @Override
    public LinchPinTool forNode(@NonNull Node node, TaskListener taskListener) throws IOException, InterruptedException {
        return new LinchPinTool(getName(),translateFor(node,taskListener),getProperties().toList());
    }

    @Override
    public void buildEnvVars(EnvVars env) {
        env.put("LINCHPIN_HOME",getHome() + "/venv");
        env.put("PATH+LINCHPIN", getHome() + "/venv/bin");
    }

    @Extension @Symbol("linchPin")
    public static class DescriptorImpl extends ToolDescriptor<LinchPinTool> {
        public DescriptorImpl() {
            load();
        }

        @Override
        public String getDisplayName() {
            return "LinchPin";
        }

        @Override
        public void setInstallations(LinchPinTool... installations) {
            super.setInstallations(installations);
            save();
        }
    }
}
