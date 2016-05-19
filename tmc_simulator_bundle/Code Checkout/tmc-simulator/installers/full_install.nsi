; full_install.nsi

; The name of the installer
Name "Full Script Installer"

; The file to write
OutFile "full_install.exe"

;--------------------------------

; Pages

Page instfiles

UninstPage uninstConfirm
UninstPage instfiles

;--------------------------------

; Full Script Install
Section "Full Script"

  ReadRegStr $0 HKLM "Software\TMC Simulator" "Install_Dir"

  StrCmp $0 "" 0 Install
  MessageBox MB_OK "TMC Simulator Software has not been installed. \
                    Exiting Installation."
  Quit     
  
  Install: 
    ; Copy script files  
    SetOutPath $0\scripts\xml
    File ..\scripts\xml\full_script.xml
    File ..\scripts\xml\script.dtd
    SetOutPath $0\scripts\pdf
    File ..\scripts\pdf\full*.pdf
    
    ; Copy audio files
    SetOutPath $0\audio\187
    File ..\audio\187\*.*
    SetOutPath $0\audio\188
    File ..\audio\188\*.*
    SetOutPath $0\audio\189
    File ..\audio\189\*.*
    SetOutPath $0\audio\190
    File ..\audio\190\*.*
    SetOutPath $0\audio\191
    File ..\audio\191\*.*
    
  
SectionEnd
