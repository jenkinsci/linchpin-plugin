import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.tools.ToolInstallation;
import hudson.tools.ToolInstaller;
import hudson.tools.ToolInstallerDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.net.URL;
/**
 * @author Aviel
 */
public class linchPinInstaller extends ToolInstaller {
    @DataBoundConstructor
    public linchPinInstaller(String label) {
        super(label);
    }

    @Override
    public FilePath performInstallation(ToolInstallation tool, Node node, TaskListener listener)
            throws IOException, InterruptedException {
        FilePath install = preferredLocation(tool, node);
        if(System.getProperty("os.name").indexOf("nux")>=0||System.getProperty("os.name").indexOf("nix")>=0){
            if (!install.exists()){
                print(listener,"Starting LinchPin Installation at "+ install);

                FilePath requirement = install.child("requirement");
                requirement.mkdirs();

                installation(listener,requirement,node);

                print(listener,"LinchPin installed successfully");
                requirement.deleteRecursive();
            }
        }else {
            print(listener, "This plugin is not supporting your OS yet.");
        }
        return install;
    }

    /**
     * Install required packages
     * @param listener
     * @param dirToInstall - directory to install
     * @throws IOException
     * @throws InterruptedException
     */
    private void installation(TaskListener listener,FilePath dirToInstall, Node node)
            throws IOException, InterruptedException {
        //Install pip
        try{
            toCmd(dirToInstall + "","wget https://bootstrap.pypa.io/get-pip.py",node,listener);
            toCmd(dirToInstall + "","python get-pip.py --user",node,listener);
        }catch (Exception e){
            print(listener, "Skipping installation - pip "+e.getMessage());
        }

        //Install virtualenv
        try{
            toCmd(dirToInstall+ "",getLinchPinHome(dirToInstall)+"/.local/bin/pip install --user virtualenv",node,listener);
            toCmd(dirToInstall.getParent()+ "", getLinchPinHome(dirToInstall)+"/.local/bin/virtualenv venv",node,listener);
        }catch (Exception e){
            print(listener, "Skipping installation - virtualenv "+e.getMessage());
        }

        //Install linchpin
        try{
            toCmd(dirToInstall.getParent()+ "/venv","bin/pip install linchpin",node,listener);
        }catch (Exception e){
            print(listener, "Skipping installation - LinchPin "+e.getMessage());
        }

        //Initial linchpin
        try{
            toCmd(dirToInstall.getParent()+ "/venv","bin/linchpin init",node,listener);
        }catch (Exception e){
            print(listener, "Skipping initialization - LinchPin "+e.getMessage());
        }
    }

    /**
     * Get the home dir for this specific installation
     * @param dirToInstall
     * @return
     */
    private String getLinchPinHome(FilePath dirToInstall){
        return dirToInstall.getParent().getParent().getParent().getParent() + "";
    }

    /**
     * Help method used to install required packages
     * @param listener - to generate logs
     * @param install - where the package will be installed
     * @param path - path to download from
     * @param msg - message to log
     * @throws IOException
     * @throws InterruptedException
     * @deprecated
     */
    private void installIfnecessaryFromURL(TaskListener listener,FilePath install,String msg,String path)
            throws IOException, InterruptedException {
        URL link = new URL(path);
        install.installIfNecessaryFrom(link,listener,msg);
    }

    /**
     * Help method to print to log
     * @param listener
     * @param msg
     */
    private void print(TaskListener listener,String msg){
        listener.getLogger().println(msg);
    }

    /**
     * Help method to launch commands to cmd
     * @param pwd - the dir that the command is running in
     * @param command - the command itself
     * @param node - the node to run the command on
     * @param listener
     * @throws IOException
     * @throws InterruptedException
     */
    private void toCmd(String pwd,String command,Node node, TaskListener listener)
            throws IOException, InterruptedException{
        Launcher.ProcStarter starter = node.createLauncher(listener).launch().cmds(command.split(" "));
        int exit = starter.pwd(pwd).stdout(listener).join();
        if(exit!=0) listener.getLogger().println("Exit code is " + exit);
    }

    @Extension
    public static class DescriptorImpl extends ToolInstallerDescriptor<linchPinInstaller>{
        @Override
        public String getDisplayName() { return "LinchPin Installer"; }

        @Override
        public boolean isApplicable(Class<? extends ToolInstallation> toolType) {
            return toolType == linchPinTool.class;
        }
    }
}
