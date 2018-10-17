# Jenkins LinchPin Plugin
![](https://img.shields.io/badge/version-1.0-blue.svg)
![](https://img.shields.io/badge/license-Apache--2.0-brightgreen.svg)
![](https://img.shields.io/badge/requirements-python_2.6%20%7C%20python_2.7-red.svg)
![](https://travis-ci.com/Avielyo10/linchpin-plugin.svg?branch=develop)   
This plugin integrate [LinchPin](https://github.com/CentOS-PaaS-SIG/linchpin) with Jenkins.

## Usage

### LinchPin Installation 
Select **`"Install automatically"`** > **`"Add Installer"`** > **`"LinchPin Installer"`**.
 
![](src/main/resources/readme/linchPinInstallation.jpg) 
  
### Build Environment 
Select **`"Use LinchPin"`** then find your LinchPin and copy&paste your PinFile.  
  
 
![](src/main/resources/readme/linchPinBuildEnv.png)  

### Build  
Select **`"Add build step"`** > **`"LinchPin Up"`**.  

![](src/main/resources/readme/linchPinBuild.png)  

### Post-build Actions  
**Very Important!** Select **`"Add post-build action"`** > **`"LinchPin TearDown"`**
If not selected LinchPin will continue to run! 

![](src/main/resources/readme/linchPinPostBuild.png)

## Adding Layout & Topology files  
For adding layout & topology files select **`"This project is parameterized"`** on **`"General"`**.  
Then **`"Add Parameter"`** > **`"File Parameter"`**.  

**Adding layout file** - on the **`"File location"`** enter first _`"layouts/"`_ then the file name you want it to be called, **don't forget the file extention!** 
 
![](src/main/resources/readme/layouts.jpg) 

**Adding topology file** - on the **`"File location"`** enter first _`"topologies/"`_ then the file name you want it to be called, **don't forget the file extention!**
 
![](src/main/resources/readme/topo.jpg) 

**Common Error :** entering _`"/layouts/yourFile.yml"`_ **won't work!** > use _`"layouts/yourFile.yml"`_ instead (without the first '/').  



