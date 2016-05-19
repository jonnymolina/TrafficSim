!define COMMON_STARTFOLDER Mapster

# Variables
Var JAVA_HOME
Var JAVA_VER
Var JAVA_INSTALLATION_MSG

# startmenu macro
!macro CUSTOM_PAGE_STARTMENU ID VAR

  !verbose push
  !verbose ${MUI_VERBOSE}

  !insertmacro MUI_PAGE_INIT

  !insertmacro MUI_SET MUI_${MUI_PAGE_UNINSTALLER_PREFIX}STARTMENUPAGE
  !insertmacro MUI_DEFAULT MUI_STARTMENUPAGE_DEFAULTFOLDER \
   "${COMMON_STARTFOLDER}\$(^Name)"
  !insertmacro MUI_DEFAULT MUI_STARTMENUPAGE_TEXT_TOP "$(MUI_${MUI_PAGE_UNINSTALLER_PREFIX}INNERTEXT_STARTMENU_TOP)"
  !insertmacro MUI_DEFAULT MUI_STARTMENUPAGE_TEXT_CHECKBOX "$(MUI_${MUI_PAGE_UNINSTALLER_PREFIX}INNERTEXT_STARTMENU_CHECKBOX)"

  !define MUI_STARTMENUPAGE_VARIABLE "${VAR}"
  !define "MUI_STARTMENUPAGE_${ID}_VARIABLE" "${MUI_STARTMENUPAGE_VARIABLE}"
  !define "MUI_STARTMENUPAGE_${ID}_DEFAULTFOLDER" "${MUI_STARTMENUPAGE_DEFAULTFOLDER}"
  !ifdef MUI_STARTMENUPAGE_REGISTRY_ROOT
    !define "MUI_STARTMENUPAGE_${ID}_REGISTRY_ROOT" "${MUI_STARTMENUPAGE_REGISTRY_ROOT}"
  !endif
  !ifdef MUI_STARTMENUPAGE_REGISTRY_KEY
    !define "MUI_STARTMENUPAGE_${ID}_REGISTRY_KEY" "${MUI_STARTMENUPAGE_REGISTRY_KEY}"
  !endif
  !ifdef MUI_STARTMENUPAGE_REGISTRY_VALUENAME
    !define "MUI_STARTMENUPAGE_${ID}_REGISTRY_VALUENAME" "${MUI_STARTMENUPAGE_REGISTRY_VALUENAME}"
  !endif

  PageEx ${MUI_PAGE_UNINSTALLER_FUNCPREFIX}custom

    PageCallbacks ${MUI_PAGE_UNINSTALLER_FUNCPREFIX}mui.StartmenuPre_${MUI_UNIQUEID} ${MUI_PAGE_UNINSTALLER_FUNCPREFIX}mui.StartmenuLeave_${MUI_UNIQUEID}

    Caption " "

  PageExEnd

  !insertmacro MUI_FUNCTION_STARTMENUPAGE ${MUI_PAGE_UNINSTALLER_FUNCPREFIX}mui.StartmenuPre_${MUI_UNIQUEID} ${MUI_PAGE_UNINSTALLER_FUNCPREFIX}mui.StartmenuLeave_${MUI_UNIQUEID}

  !undef MUI_STARTMENUPAGE_VARIABLE
  !undef MUI_STARTMENUPAGE_TEXT_TOP
  !undef MUI_STARTMENUPAGE_TEXT_CHECKBOX
  !undef MUI_STARTMENUPAGE_DEFAULTFOLDER
  
  !undef MUI_STARTMENUPAGE_${ID}_REGISTRY_ROOT
  !undef MUI_STARTMENUPAGE_${ID}_REGISTRY_KEY
  !undef MUI_STARTMENUPAGE_${ID}_REGISTRY_VALUENAME
  
  !insertmacro MUI_UNSET MUI_STARTMENUPAGE_NODISABLE
  !insertmacro MUI_UNSET MUI_STARTMENUPAGE_REGISTRY_ROOT
  !insertmacro MUI_UNSET MUI_STARTMENUPAGE_REGISTRY_KEY
  !insertmacro MUI_UNSET MUI_STARTMENUPAGE_REGISTRY_VALUENAME

  !verbose pop

!macroend

# Installer functions
Function JavaCheck
   Call LocateJVM
   StrCmp "" $JAVA_INSTALLATION_MSG Done Failure
 
   Failure:
      Push $0
      StrCpy $0 "$JAVA_INSTALLATION_MSG$\n$\nYou can still install Mapster \
         but the program will not run without the correct JRE.$\n$\nDo you \
         want to continue?"
      MessageBox MB_YESNO $0 IDYES Done
         Abort
      Pop $0
         
   Done:
      InitPluginsDir
FunctionEnd

Function LocateJVM
   ;Check for Java version and location
   Push $0
   Push $1
   Push $2
    
   ReadRegStr $JAVA_VER HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" \
      CurrentVersion
   StrCmp "" "$JAVA_VER" JavaNotPresent CheckJavaVer
 
   JavaNotPresent:
      StrCpy $JAVA_INSTALLATION_MSG "Java Runtime Environment is not installed \
         on your computer. You need version 1.5 or newer to run Mapster {APP}."
      Goto Done
 
   CheckJavaVer:
      ReadRegStr $0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$JAVA_VER"\
         JavaHome
      GetFullPathName /SHORT $JAVA_HOME "$0"
      StrCpy $0 $JAVA_VER 1 0
      StrCpy $1 $JAVA_VER 1 2
      StrCpy $JAVA_VER "$0$1"
      IntCmp ${MIN_JAVA} $JAVA_VER FoundCorrectJavaVer FoundCorrectJavaVer \
         JavaVerNotCorrect
        
   FoundCorrectJavaVer:
      IfFileExists "$JAVA_HOME\bin\javaw.exe" 0 JavaNotPresent      
      Goto Done
        
   JavaVerNotCorrect:
      StrCpy $JAVA_VER "$0.$1"
      StrCpy $0 ${MIN_JAVA} 1 0
      StrCpy $1 ${MIN_JAVA} 1 1
      StrCpy $2 "$0.$1"
      StrCpy $JAVA_INSTALLATION_MSG "The version of Java Runtime Environment \
          installed on your computer is $JAVA_VER. Version $2 or newer is \
          required to run Mapster {APP}."
   Done:
      Pop $2
      Pop $1
      Pop $0
FunctionEnd

; File needs to be opened in $0
Function WriteNewline
   FileWriteByte $0 "13"
   FileWriteByte $0 "10"
FunctionEnd

Function un.Tokenize
   Tokenize:
      Push "\"
      Call un.StrTok
      Pop $R0
      StrCmp $R1 "" Yes No
      Yes:      
         StrCpy $R1 $R0  
         Goto Continue
      No:
         StrCmp $R0 "" EndTokenize 0
         StrCpy $R1 "$R1\$R0"   
            
      Continue:
      StrCmp $R0 ""  EndTokenize 0      
      Push $R1
      Exch   
      Goto Tokenize
   EndTokenize:
FunctionEnd

;author bigmac666
Function un.StrTok
  Exch $R1
  Exch 1
  Exch $R0
  Push $R2
  Push $R3
  Push $R4
  Push $R5
  
  ;R0 fullstring
  ;R1 tokens
  ;R2 len of fullstring
  ;R3 len of tokens
  ;R4 char from string
  ;R5 testchar
  
  StrLen $R2 $R0
  IntOp $R2 $R2 + 1
 
  loop1:
    IntOp $R2 $R2 - 1
    IntCmp $R2 0 exit
 
    StrCpy $R4 $R0 1 -$R2
 
    StrLen $R3 $R1
    IntOp $R3 $R3 + 1
 
    loop2:
      IntOp $R3 $R3 - 1
      IntCmp $R3 0 loop1
 
      StrCpy $R5 $R1 1 -$R3
 
      StrCmp $R4 $R5 Found
    Goto loop2
  Goto loop1
 
  exit:
  ;Not found!!!
  StrCpy $R1 ""
  StrCpy $R0 ""
  Goto Cleanup
 
  Found:
  StrLen $R3 $R0
  IntOp $R3 $R3 - $R2
  StrCpy $R1 $R0 $R3
 
  IntOp $R2 $R2 - 1
  IntOp $R3 $R3 + 1
  StrCpy $R0 $R0 $R2 $R3
 
  Cleanup:
  Pop $R5
  Pop $R4
  Pop $R3
  Pop $R2
  Exch $R0
  Exch 1
  Exch $R1
 
FunctionEnd