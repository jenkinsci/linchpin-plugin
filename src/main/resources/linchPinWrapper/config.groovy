
f=namespace(lib.FormTagLib)

def installations = app.getDescriptorByType(Installation.linchPinTool.DescriptorImpl).installations

if (installations.length != 0) {
    f.entry(title: _('Which LinchPin:')) {
        select(class: 'setting-input', name: 'Installation.linchPinTool.installation') {
            installations.each { install ->
                f.option(selected: install.name == instance?.installation, value: install.name){
                    text(install.name)
                }
            }
        }
    }
    f.entry(title:_("PinFile:"),field:"pinFile") {
        f.textarea()
    }
//    f.advanced(title:_("Create topology and layout files")){
//        f.entry(title:_("Topology File Name:"),field:"topologyFileName"){
//            f.textbox()
//        }
//        f.entry(title:_("Topology File:"),field:"topologyFile") {
//            f.textarea()
//        }
//        f.entry(title:_("Layout File Name:"),field:"layoutFileName"){
//            f.textbox()
//        }
//        f.entry(title:_("Layout File:"),field:"layoutFile") {
//            f.textarea()
//        }
//    }
}
