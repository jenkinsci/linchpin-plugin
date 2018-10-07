
f=namespace(lib.FormTagLib)

def installations = app.getDescriptorByType(Installation.linchPinTool.DescriptorImpl).installations
if (installations.length != 0) {
    f.entry(title: _('Select Tool')) {
        select(class: 'setting-input', name: 'Installation.linchPinTool.installation') {
            installations.each { install ->
                f.option(selected: install.name == instance?.installation, value: install.name){
                    text(install.name)
                }
            }
        }
    }
    f.entry(title:_("PinFile"),field:"pinfile") {
        f.expandableTextbox()
    }
}
