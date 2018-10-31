# Jenkins LinchPin Plugin
![](https://img.shields.io/badge/version-1.0-blue.svg)
![](https://img.shields.io/badge/license-Apache--2.0-brightgreen.svg)
![](https://img.shields.io/badge/requirements-python_2.6%20%7C%20python_2.7-red.svg)
![](https://travis-ci.com/Avielyo10/linchpin-plugin.svg?branch=develop)   
This plugin integrate [LinchPin](https://github.com/CentOS-PaaS-SIG/linchpin) & [Cinch](https://github.com/RedHatQE/cinch) with Jenkins.

## Usage

### LinchPin Installation 
Select **`"Install automatically"`** > **`"Add Installer"`** > **`"LinchPin Installer"`**.
 
![](src/main/resources/readme/linchPinInstallation.jpg) 
  
### Build Environment 
Select **`"Use LinchPin"`** then find your LinchPin and copy&paste your PinFile.  
  
 
![](src/main/resources/readme/linchPinBuildEnv.png)  

### Build  
Select **`"Add build step"`** > **`"LinchPin Up"`**.   
**Optional:** **`"Cinch Up"`**, **`"Insert Inventory"`** is mandatory if selected. 

![](src/main/resources/readme/linchPinBuild.png)  

### Post-build Actions  
**Very Important!** Select **`"Add post-build action"`** > **`"LinchPin Teardown"`** & **`"Teardown Cinch"`** with the proper inventory if **`"Cinch Up"`** was selected on **Build**.
   
If not selected **LinchPin & Cinch** will continue to run! 

![](src/main/resources/readme/linchPinPostBuild.png)

## Adding Files To LinchPin & Cinch  
For adding  files select **`"This project is parameterized"`** on **`"General"`**.  
Then **`"Add Parameter"`** > **`"File Parameter"`**.  

Possible **`"File location"`**s are: 
- _`"layouts/"`_.
- _`"topologies/"`_.
- _`"inventories/"`_. 
- _`"credentials/"`_.
- _`"hooks/"`_.
- _`"resources/"`_. 

---
###**Example**  
**Adding layout file** - on the **`"File location"`** enter first _`"layouts/"`_ then the file name you want it to be called, **don't forget the file extention!** 
 
![](src/main/resources/readme/layouts.jpg) 



---
**Common Error :** entering _`"/layouts/yourFile.yml"`_ **won't work!** > use _`"layouts/yourFile.yml"`_ instead (without the first '/').  

---

## Documentations

- [LinchPin documentation](https://linchpin.readthedocs.io/en/latest/).
- [Cinch documentation](https://redhatqe-cinch.readthedocs.io/en/latest/index.html).