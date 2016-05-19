; The name of the installer
Name "Demo Script Installer"

; The file to write
OutFile "demo_install.exe"

;--------------------------------

; Pages

Page instfiles

UninstPage uninstConfirm
UninstPage instfiles

;--------------------------------

; Full Script Install
Section "Demo Script"

  ReadRegStr $0 HKLM "Software\TMC Simulator" "Install_Dir"

  StrCmp $0 "" 0 Install
  MessageBox MB_OK "TMC Simulator Software has not been installed. \
                    Exiting Installation."
  Quit     
  
  Install: 
    ; Copy script files  
    SetOutPath $0\scripts\xml
    File ..\scripts\xml\demo_script.xml
    File ..\scripts\xml\script.dtd
   
SectionEnd
