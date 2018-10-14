package util;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;
import jenkins.tasks.SimpleBuildWrapper;

import java.io.*;

public class linchPinUtil {
    /**
     * Read the /tmp/linchpin.out file to see if previous LinchPin is still running
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
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
    public void toCmd(String pwd, String command, Launcher launcher, TaskListener listener)
            throws IOException, InterruptedException{
        Launcher.ProcStarter starter = launcher.launch().cmds(command.split(" "));
        int exit = starter.pwd(pwd).stdout(listener).join();
        if(exit!=0) listener.getLogger().println("Exit code is " + exit);
    }
    /**
     * Help method to launch commands to cmd
     * @param pwd - the dir that the command is running in
     * @param command - the command itself
     * @param launcher
     * @param listener
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    public void toCmd(String pwd, String command, Launcher launcher, TaskListener listener, SimpleBuildWrapper.Context context)
            throws IOException, InterruptedException{
        Launcher.ProcStarter starter = launcher.launch().cmds(command.split(" "));
        int exit = starter.pwd(pwd).envs(context.getEnv()).stdout(listener).join();
        if(exit!=0) listener.getLogger().println("Exit code is " + exit);
    }

    /**
     * Help method to handle the case the user didn't used the teardown on post-build.
     * It prevents running more then one LinchPin on the same machine.
     * @param pathToPrevInstallation
     * @param launcher
     * @param listener
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    public void tearDownPrevLinchPin(String pathToPrevInstallation, Launcher launcher, TaskListener listener, SimpleBuildWrapper.Context context)
            throws IOException, InterruptedException{
        listener.error("Please Use LinchPin TearDown Option At The Post-Build Action!");
        toCmd(pathToPrevInstallation,"bin/linchpin destroy",launcher,listener,context);
    }

    /**
     * Create files for linchpin configuration
     * @param content
     * @param path
     * @param name
     * @throws IOException
     */
    public void createFile(String content, String path, String name) throws IOException{
        if(content == null) return;
        if(name == null) name = "defaultName.yml";
        String fileName = path+name;
        if(!fileName.endsWith(".yml")&&!fileName.endsWith(".out")) fileName+=".yml";
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(content+"\n");
        writer.close();
    }
}
