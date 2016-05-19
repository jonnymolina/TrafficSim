; practice_install.nsi

; The name of the installer
Name "Practice Script Installer"

; The file to write
OutFile "practice_install.exe"

;--------------------------------

; Pages

Page instfiles

UninstPage uninstConfirm
UninstPage instfiles

;--------------------------------

; Practice Script Install
Section "Practice Script"

  ReadRegStr $0 HKLM "Software\TMC Simulator" "Install_Dir"

  StrCmp $0 "" 0 Install
  MessageBox MB_OK "TMC Simulator Software has not been installed. \
                    Exiting Installation."
  Quit     
  
  Install: 
    ; Copy script files  
    SetOutPath $0\scripts\xml
    File ..\scripts\xml\practice_script.xml
    File ..\scripts\xml\script.dtd
    SetOutPath $0\scripts\pdf
    File ..\scripts\pdf\practice*.pdf
    
    ; Copy audio files
    SetOutPath $0\audio\181
    File ..\audio\181\*.*
    
  
SectionEnd
