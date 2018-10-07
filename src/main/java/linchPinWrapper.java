import hudson.*;
import hudson.model.*;
import hudson.slaves.NodeSpecific;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.tools.ToolInstallation;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildWrapper;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.IOException;
import java.util.Map;

public class linchPinWrapper extends SimpleBuildWrapper {
    private String installation;

    @DataBoundConstructor
    public linchPinWrapper() {}

    public String getInstallation() {
        return installation;
    }

    @DataBoundSetter
    public void setInstallation(String installation) {
        this.installation = Util.fixEmpty(installation);
    }

    @Override
    public void setUp(Context context, Run<?, ?> build, FilePath workspace, Launcher launcher, TaskListener listener, EnvVars initialEnvironment) throws IOException, InterruptedException {
        if(installation != null){
            ToolInstallation[] tools = Jenkins.getActiveInstance().getDescriptorByType(linchPinTool.DescriptorImpl.class).getInstallations();
            ToolInstallation inst = null;
            for (ToolInstallation _inst : tools) {
                if (_inst.getName().equals(installation)) {
                    inst = _inst;
                    break;
                }
            }
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
