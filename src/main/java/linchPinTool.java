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
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.List;

/**
 * @author Aviel
 */
public class linchPinTool extends ToolInstallation
        implements NodeSpecific<linchPinTool>, EnvironmentSpecific<linchPinTool> {

    @DataBoundConstructor
    public linchPinTool(String name, String home, List<? extends ToolProperty<?>> properties) {
        super(name, home, properties);
    }

    @Override
    public linchPinTool forEnvironment(EnvVars envVars) {
        return new linchPinTool(getName(),envVars.expand(getHome()),getProperties().toList());
    }

    @Override
    public linchPinTool forNode(@NonNull Node node, TaskListener taskListener) throws IOException, InterruptedException {
        return new linchPinTool(getName(),translateFor(node,taskListener),getProperties().toList());
    }

    @Override
    public void buildEnvVars(EnvVars env) {
        env.put("LINCHPIN_HOME",getHome());
        env.put("PATH+LINCHPIN", getHome() + "/venv/bin");
    }

    @Extension
    public static class DescriptorImpl extends ToolDescriptor<linchPinTool> {
        public DescriptorImpl() {
            load();
        }

        @Override
        public String getDisplayName() {
            return "LinchPin";
        }

        @Override
        public void setInstallations(linchPinTool... installations) {
            super.setInstallations(installations);
            save();
        }
    }
}
