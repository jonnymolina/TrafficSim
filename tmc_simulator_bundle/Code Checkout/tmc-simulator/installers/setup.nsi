Name "TMC Simulator"

# Defines
!define REGKEY "SOFTWARE\$(^Name)"
!define VERSION 1.20
!define COMPANY "ARDFA - Cal Poly San Luis Obispo"
!define URL ""

#Config Files
!define CAD_SIM_PROP       "cad_simulator_config.properties"
!define MEDIA_PROP         "cad_simulator_media_config.properties"
!define ATMS_PROP          "cad_simulator_atms_config.properties"
!define PARAMICS_PROP      "cad_simulator_paramics_config.properties"
!define SIM_MGR_PROP       "sim_manager_config.properties"
!define PARAMICS_COMM_PROP "paramics_communicator_config.properties"
!define CAD_CLIENT_PROP    "cad_client_config.properties"

# MUI defines
!define MUI_ICON "${NSISDIR}\Contrib\Graphics\Icons\modern-install-blue.ico"
!define MUI_FINISHPAGE_NOAUTOCLOSE
!define MUI_STARTMENUPAGE_REGISTRY_ROOT HKLM
!define MUI_STARTMENUPAGE_NODISABLE
!define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\TMC Simulator"
!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME StartMenuGroup
!define MUI_STARTMENUPAGE_DEFAULT_FOLDER "TMC Simulator"
!define MUI_UNICON "${NSISDIR}\Contrib\Graphics\Icons\modern-uninstall-blue.ico"
!define MUI_UNFINISHPAGE_NOAUTOCLOSE

# Included files
!include Sections.nsh
!include MUI.nsh
!include shared.nsh

# Reserved Files

# Variables
Var StartMenuGroup
VAR cad_sim_host
VAR atms_username
VAR atms_password
VAR atms_host
VAR cad_position
VAR cad_user_id
VAR paramics_host

# Installer pages
!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_STARTMENU Application $StartMenuGroup
Page custom CreateProperties
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

# Installer languages
!insertmacro MUI_LANGUAGE English

# Installer attributes
OutFile setup.exe
InstallDir "$PROGRAMFILES\TMC Simulator"
CRCCheck on
XPStyle on
ShowInstDetails show
VIProductVersion 1.20.0.0
VIAddVersionKey ProductName "TMC Simulator"
VIAddVersionKey ProductVersion "${VERSION}"
VIAddVersionKey CompanyName "${COMPANY}"
VIAddVersionKey FileVersion ""
VIAddVersionKey FileDescription ""
VIAddVersionKey LegalCopyright ""
InstallDirRegKey HKLM "${REGKEY}" Path
ShowUninstDetails show

# Installer sections
Section -Main SEC0000
    WriteRegStr HKEY_LOCAL_MACHINE "SOFTWARE\TMC Simulator" Install_Dir $INSTDIR
    WriteRegStr HKLM "${REGKEY}\Components" Main 1x
SectionEnd

Section "CAD Simulator" SEC0001

    SetOutPath $INSTDIR\CADSimulator
    SetOverwrite on
    File ..\build\CADSimulator.exe
    File ..\build\plink.exe
    SetOutPath $INSTDIR\CADSimulator\lib\xerces-2_8_0
    File ..\lib\xerces-2_8_0\xercesImpl.jar
    SetOutPath $INSTDIR\CADSimulator\config
    File ..\config\cad_simulator_config.properties
    File ..\config\cad_simulator_logging.properties
    File ..\config\cad_simulator_atms_config.properties
    File ..\config\cad_simulator_media_config.properties
    File ..\config\cad_simulator_paramics_config.properties
    File ..\config\cmsdiversions.xml
    File ..\config\dvdplayers.xml
    File ..\config\stillimages.xml
    CreateDirectory $INSTDIR\CADSimulator\logs
    SetOutPath "$SMPROGRAMS\TMC Simulator"
    CreateShortcut "$SMPROGRAMS\TMC Simulator\CAD Simulator.lnk" $INSTDIR\CADSimulator\CADSimulator.exe
    WriteRegStr HKLM "${REGKEY}\Components" "CAD Simulator" 1
    
    call CreateCADSimulatorProperties
    call CreateMediaProperties
    call CreateATMSProperties
    call CreateParamicsProperties
SectionEnd

Section "Simulation Manager" SEC0002
    SetOutPath $INSTDIR\SimulationManager
    SetOverwrite on
    File ..\build\SimulationManager.exe
    SetOutPath $INSTDIR\SimulationManager\config
    File ..\config\sim_manager_config.properties
    File ..\config\sim_manager_logging.properties
    CreateDirectory $INSTDIR\SimulationManager\logs
    SetOutPath "$SMPROGRAMS\TMC Simulator"
    CreateShortcut "$SMPROGRAMS\TMC Simulator\Simulation Manager.lnk" $INSTDIR\SimulationManager\SimulationManager.exe
    WriteRegStr HKLM "${REGKEY}\Components" "Simulation Manager" 1
            
    call CreateSimManagerProperties
SectionEnd

Section "Paramics Communicator" SEC0003
    SetOutPath $INSTDIR\ParamicsCommunicator
    SetOverwrite on
    File ..\build\ParamicsCommunicator.exe
    SetOutPath $INSTDIR\ParamicsCommunicator\lib\xerces-2_8_0
    File ..\lib\xerces-2_8_0\xercesImpl.jar
    SetOutPath $INSTDIR\ParamicsCommunicator\config
    File ..\config\paramics_communicator_config.properties
    File ..\config\paramics_communicator_logging.properties
    CreateDirectory $INSTDIR\ParamicsCommunicator\logs
    SetOutPath "$SMPROGRAMS\TMC Simulator"
    CreateShortcut "$SMPROGRAMS\TMC Simulator\Paramics Communicator.lnk" $INSTDIR\ParamicsCommunicator\ParamicsCommunicator.exe
    CreateShortcut "$SMPROGRAMS\TMC Simulator\Paramics Modeler.lnk" c:\tmc_simulator\tmc_batch.bat
    WriteRegStr HKLM "${REGKEY}\Components" "Paramics Communicator" 1
    
    call CreateParamicsCommunicatorProperties
SectionEnd

Section "CAD Client" SEC0004
    SetOutPath $INSTDIR\CADClient
    SetOverwrite on
    File ..\build\CADClient.exe
    SetOutPath $INSTDIR\CADClient\lib\xerces-2_8_0
    File ..\lib\xerces-2_8_0\xercesImpl.jar
    SetOutPath $INSTDIR\CADClient\config
    File ..\config\cad_client_config.properties
    File ..\config\cad_client_logging.properties
    CreateDirectory $INSTDIR\CADClient\logs
    SetOutPath "$SMPROGRAMS\TMC Simulator"
    CreateShortcut "$SMPROGRAMS\TMC Simulator\CAD Client.lnk" $INSTDIR\CADClient\CADClient.exe
    WriteRegStr HKLM "${REGKEY}\Components" "CAD Client" 1
    
    call CreateCADClientProperties
SectionEnd

Function CreateProperties

     !insertmacro SectionFlagIsSet ${SEC0002} ${SF_SELECTED} SimManDialogs CADClientSection

  CADClientSection:
     !insertmacro SectionFlagIsSet ${SEC0004} ${SF_SELECTED} CADClientDialogs PropertiesDone

  SimManDialogs:
     !insertmacro MUI_INSTALLOPTIONS_DISPLAY "cad_simulator_host.ini"
     ReadINIStr $cad_sim_host "$PLUGINSDIR\cad_simulator_host.ini" "Field 1" "State"
    
     !insertmacro MUI_INSTALLOPTIONS_DISPLAY "atms_host.ini"
     ReadINIStr $atms_host "$PLUGINSDIR\atms_host.ini" "Field 1" "State"
     
     !insertmacro MUI_INSTALLOPTIONS_DISPLAY "atms_properties.ini"     
     ReadINIStr $atms_username "$PLUGINSDIR\atms_properties.ini" "Field 1" "State"
     ReadINIStr $atms_password "$PLUGINSDIR\atms_properties.ini" "Field 4" "State"
     
     !insertmacro MUI_INSTALLOPTIONS_DISPLAY "paramics_host.ini"     
     ReadINIStr $paramics_host "$PLUGINSDIR\paramics_host.ini" "Field 1" "State"     
      
     Goto CADClientSection
     
  CADClientDialogs:

     !insertmacro MUI_INSTALLOPTIONS_DISPLAY "cad_client_properties.ini"     
     ReadINIStr $cad_position "$PLUGINSDIR\cad_client_properties.ini" "Field 1" "State"     
     ReadINIStr $cad_user_id  "$PLUGINSDIR\cad_client_properties.ini" "Field 4" "State"     

     !insertmacro SectionFlagIsSet ${SEC0002} ${SF_SELECTED} PropertiesDone DoDialog 
     DoDialog:
        !insertmacro MUI_INSTALLOPTIONS_DISPLAY "cad_simulator_host.ini"
        ReadINIStr $cad_sim_host "$PLUGINSDIR\cad_simulator_host.ini" "Field 1" "State"


  PropertiesDone:
FunctionEnd

Function CreateCADSimulatorProperties
   Push $0
   
   DetailPrint "Creating ${CAD_SIM_PROP}"
   FileOpen $0 "$INSTDIR\CADSimulator\config\${CAD_SIM_PROP}" w

   FileWrite $0 "CADClientPort          = 4444"
   Call WriteNewline
   FileWrite $0 "CoordinatorRMIPort     = 4445"
   Call WriteNewline      
   FileWrite $0 "CMSDiversionXML        = config/cmsdiversions.xml"
   Call WriteNewline   
   FileWrite $0 "AudioFileLocation      = ../../audio/"
   Call WriteNewline   
   FileWrite $0 "ParamicsProperties     = config/cad_simulator_paramics_config.properties"
   Call WriteNewline   
   FileWrite $0 "ATMSProperties         = config/cad_simulator_atms_config.properties"
   Call WriteNewline   
   FileWrite $0 "MediaProperties        = config/cad_simulator_media_config.properties"
   Call WriteNewline      
   
   FileClose $0
   
   Pop $0   
FunctionEnd

Function CreateMediaProperties
   Push $0
   
   DetailPrint "Creating ${MEDIA_PROP}"
   FileOpen $0 "$INSTDIR\CADSimulator\config\${MEDIA_PROP}" w

   FileWrite $0 "DVDPlayerXML           = config/dvdplayers.xml"
   Call WriteNewline
   FileWrite $0 "StillImagesXML         = config/stillimages.xml"
   Call WriteNewline      
   
   FileClose $0
   
   Pop $0   
FunctionEnd

Function CreateATMSProperties 
   Push $0
   
   DetailPrint "Creating ${ATMS_PROP}"
   FileOpen $0 "$INSTDIR\CADSimulator\config\${ATMS_PROP}" w

   FileWrite $0 "ATMSHost = $atms_host"
   Call WriteNewline
   FileWrite $0 "Username = $atms_username"
   Call WriteNewline      
   FileWrite $0 "Password = $atms_password"
   Call WriteNewline   
   FileWrite $0 "ImageDir = /opt/d12uci/user_config/cctv"
   Call WriteNewline   
   
   FileClose $0
   
   Pop $0   
FunctionEnd

Function CreateParamicsProperties
   Push $0
   
   DetailPrint "Creating ${PARAMICS_PROP}"
   FileOpen $0 "$INSTDIR\CADSimulator\config\${PARAMICS_PROP}" w

   FileWrite $0 "ParamicsCommHost       = $paramics_host"
   Call WriteNewline
   FileWrite $0 "ParamicsCommPort       = 4450"
   Call WriteNewline      
   FileWrite $0 "IncidentUpdateInterval = 30"
   Call WriteNewline   
   FileWrite $0 "IncidentUpdateFile     = exchange.xml"
   Call WriteNewline   
   FileWrite $0 "ParamicsStatusInterval = 15"
   Call WriteNewline   
   FileWrite $0 "ParamicsStatusFile     = paramics_status.xml"
   Call WriteNewline      
   FileWrite $0 "CameraStatusInterval   = 30"
   Call WriteNewline    
   FileWrite $0 "CameraStatusFile       = camera_status.xml"
   Call WriteNewline    
   
   FileClose $0
   
   Pop $0   
FunctionEnd

Function CreateSimManagerProperties
   Push $0
   
   DetailPrint "Creating ${SIM_MGR_PROP}"
   FileOpen $0 "$INSTDIR\SimulationManager\config\${SIM_MGR_PROP}" w

   FileWrite $0 "CADSimulatorHost       = $cad_sim_host"
   Call WriteNewline
   FileWrite $0 "CADSimulatorRMIPort    = 4445"
   Call WriteNewline   
   FileWrite $0 "ScriptDir              = ../../scripts/xml"
   Call WriteNewline   
   FileWrite $0 "FakeParamicsConnection = false"
   Call WriteNewline   
   
   FileClose $0
   
   Pop $0   
FunctionEnd

Function CreateParamicsCommunicatorProperties
   Push $0
   
   DetailPrint "Creating ${PARAMICS_COMM_PROP}"
   FileOpen $0 "$INSTDIR\ParamicsCommunicator\config\${PARAMICS_COMM_PROP}" w
   FileWrite $0 "SocketPort       = 4450"
   Call WriteNewline      
   
   FileClose $0
   
   Pop $0   
FunctionEnd

Function CreateCADClientProperties
   Push $0
   
   DetailPrint "Creating ${CAD_CLIENT_PROP}"
   FileOpen $0 "$INSTDIR\CADClient\config\${CAD_CLIENT_PROP}" w

   FileWrite $0 "CADSimulatorHost       = $cad_sim_host"
   Call WriteNewline
   FileWrite $0 "CADSimulatorSocketPort = 4444"
   Call WriteNewline      
   FileWrite $0 "KeyboardType           = CAD"
   Call WriteNewline      
   FileWrite $0 "DisplayType            = FULL_SCREEN"
   Call WriteNewline      
   FileWrite $0 "CADPosition            = $cad_position"
   Call WriteNewline      
   FileWrite $0 "CADUserID              = $cad_user_id"
   Call WriteNewline      
   
   FileClose $0
   
   Pop $0   
FunctionEnd

Section -post SEC0005
    WriteRegStr HKLM "${REGKEY}" Path $INSTDIR
    WriteUninstaller $INSTDIR\uninstall.exe
    !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
    SetOutPath $SMPROGRAMS\$StartMenuGroup
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\Uninstall $(^Name).lnk" $INSTDIR\uninstall.exe
    !insertmacro MUI_STARTMENU_WRITE_END
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayName "$(^Name)"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayVersion "${VERSION}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" Publisher "${COMPANY}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayIcon $INSTDIR\uninstall.exe
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" UninstallString $INSTDIR\uninstall.exe
    WriteRegDWORD HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" NoModify 1
    WriteRegDWORD HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" NoRepair 1
SectionEnd

# Macro for selecting uninstaller sections
!macro SELECT_UNSECTION SECTION_NAME UNSECTION_ID
    Push $R0
    ReadRegStr $R0 HKLM "${REGKEY}\Components" "${SECTION_NAME}"
    StrCmp $R0 1 0 next${UNSECTION_ID}
    !insertmacro SelectSection "${UNSECTION_ID}"
    GoTo done${UNSECTION_ID}
next${UNSECTION_ID}:
    !insertmacro UnselectSection "${UNSECTION_ID}"
done${UNSECTION_ID}:
    Pop $R0
!macroend

# Uninstaller sections
Section /o "un.CAD Client" UNSEC0004
    Delete /REBOOTOK "$SMPROGRAMS\TMC Simulator\CAD Client.lnk"
    Delete /REBOOTOK $INSTDIR\CADClient\config\cad_client_config.properties
    Delete /REBOOTOK $INSTDIR\CADClient\config\cad_client_logging.properties
    Delete /REBOOTOK $INSTDIR\CADClient\lib\xerces-2_8_0\xercesImpl.jar
    Delete /REBOOTOK $INSTDIR\CADClient\CADClient.exe
    DeleteRegValue HKLM "${REGKEY}\Components" "CAD Client"
SectionEnd

Section /o "un.Paramics Communicator" UNSEC0003
    Delete /REBOOTOK "$SMPROGRAMS\TMC Simulator\Paramics Communicator.lnk"
    Delete /REBOOTOK "$SMPROGRAMS\TMC Simulator\Paramics Modeler.lnk"
    Delete /REBOOTOK $INSTDIR\ParamicsCommunicator\config\paramics_communicator_config.properties
    Delete /REBOOTOK $INSTDIR\ParamicsCommunicator\config\paramics_communicator_logging.properties
    Delete /REBOOTOK $INSTDIR\ParamicsCommunicator\lib\xerces-2_8_0\xercesImpl.jar
    Delete /REBOOTOK $INSTDIR\ParamicsCommunicator\ParamicsCommunicator.exe
    DeleteRegValue HKLM "${REGKEY}\Components" "Paramics Communicator"
SectionEnd

Section /o "un.Simulation Manager" UNSEC0002
    SetOutPath $0\scripts\xml
    SetOutPath $0\scripts\pdf
    
    ; Copy audio files
    SetOutPath $0\audio\187


    Delete /REBOOTOK "$SMPROGRAMS\TMC Simulator\Simulation Manager.lnk"
    Delete /REBOOTOK $INSTDIR\SimulationManager\config\sim_manager_config.properties
    Delete /REBOOTOK $INSTDIR\SimulationManager\config\sim_manager_logging.properties
    Delete /REBOOTOK $INSTDIR\SimulationManager\SimulationManager.exe
    DeleteRegValue HKLM "${REGKEY}\Components" "Simulation Manager"
SectionEnd

Section /o "un.CAD Simulator" UNSEC0001
    Delete /REBOOTOK "$SMPROGRAMS\TMC Simulator\CAD Simulator.lnk"
    Delete /REBOOTOK $INSTDIR\CADSimulator\config\stillimages.xml
    Delete /REBOOTOK $INSTDIR\CADSimulator\config\dvdplayers.xml
    Delete /REBOOTOK $INSTDIR\CADSimulator\config\cmsdiversions.xml
    Delete /REBOOTOK $INSTDIR\CADSimulator\config\cad_simulator_config.properties
    Delete /REBOOTOK $INSTDIR\CADSimulator\config\cad_simulator_logging.properties
    Delete /REBOOTOK $INSTDIR\CADSimulator\config\cad_simulator_paramics_config.properties
    Delete /REBOOTOK $INSTDIR\CADSimulator\config\cad_simulator_media_config.properties
    Delete /REBOOTOK $INSTDIR\CADSimulator\config\cad_simulator_atms_config.properties
    Delete /REBOOTOK $INSTDIR\CADSimulator\lib\xerces-2_8_0\xercesImpl.jar
    Delete /REBOOTOK $INSTDIR\CADSimulator\plink.exe
    Delete /REBOOTOK $INSTDIR\CADSimulator\CADSimulator.exe
    Delete /REBOOTOK $INSTDIR\CADSimulator\logs\*
    DeleteRegValue HKLM "${REGKEY}\Components" "CAD Simulator"
SectionEnd

Section /o un.Main UNSEC0000
    DeleteRegValue HKEY_LOCAL_MACHINE "SOFTWARE\TMC Simulator" Install_Dir
    DeleteRegValue HKLM "${REGKEY}\Components" Main
SectionEnd

Section un.post UNSEC0005
    DeleteRegKey HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)"
    Delete /REBOOTOK "$SMPROGRAMS\$StartMenuGroup\Uninstall $(^Name).lnk"
    Delete /REBOOTOK $INSTDIR\uninstall.exe
    DeleteRegValue HKLM "${REGKEY}" StartMenuGroup
    DeleteRegValue HKLM "${REGKEY}" Path
    DeleteRegKey /IfEmpty HKLM "${REGKEY}\Components"
    DeleteRegKey /IfEmpty HKLM "${REGKEY}"
    RmDir /REBOOTOK $SMPROGRAMS\$StartMenuGroup
    RmDir /r /REBOOTOK $INSTDIR\scripts
    RmDir /r /REBOOTOK $INSTDIR\audio
    RmDir /REBOOTOK $INSTDIR
SectionEnd

# Installer functions
Function .onInit
   call JavaCheck

   InitPluginsDir
   File /oname=$PLUGINSDIR\cad_simulator_host.ini cad_simulator_host.ini
   File /oname=$PLUGINSDIR\atms_properties.ini atms_properties.ini
   File /oname=$PLUGINSDIR\atms_host.ini atms_host.ini
   File /oname=$PLUGINSDIR\cad_client_properties.ini cad_client_properties.ini
   File /oname=$PLUGINSDIR\paramics_host.ini paramics_host.ini
FunctionEnd

# Uninstaller functions
Function un.onInit
    ReadRegStr $INSTDIR HKLM "${REGKEY}" Path
    ReadRegStr $StartMenuGroup HKLM "${REGKEY}" StartMenuGroup
    !insertmacro SELECT_UNSECTION Main ${UNSEC0000}
    !insertmacro SELECT_UNSECTION "CAD Simulator" ${UNSEC0001}
    !insertmacro SELECT_UNSECTION "Simulation Manager" ${UNSEC0002}
    !insertmacro SELECT_UNSECTION "Paramics Communicator" ${UNSEC0003}
    !insertmacro SELECT_UNSECTION "CAD Client" ${UNSEC0004}
FunctionEnd

