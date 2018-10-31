f=namespace(lib.FormTagLib)

def installations = app.getDescriptorByType(Installation.LinchPinTool.DescriptorImpl).installations

if (installations.length != 0) {
    f.entry(title: _('Which LinchPin:')) {
        select(class: 'setting-input', name: 'Installation.LinchPinTool.installation') {
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
}