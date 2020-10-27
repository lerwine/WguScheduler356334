

#$TargetFolder = [System.Environment]::GetFolderPath([System.Environment+SpecialFolder]::UserProfile);
#$TargetFolder = 'C:\Users\lerwi\AndroidStudioProjects\WguScheduler356334\app\src\main\res\drawable-v24';
$Script:TargetFolder = 'C:\Users\lerwi\AndroidStudioProjects\WguScheduler356334\app\src\main\res\drawable';

$Script:XmlNs = @{
   dc="http://purl.org/dc/elements/1.1/";
   cc="http://creativecommons.org/ns#";
   rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
   svg="http://www.w3.org/2000/svg";
   android="http://schemas.android.com/apk/res/android"
};

Function Invoke-OpenFileDialog {
    [CmdletBinding()]
    Param()
    $OpenFileDialog = [Microsoft.Win32.OpenFileDialog]::new();
    $OpenFileDialog.RestoreDirectory = $true;

    $OpenFileDialog.Filter = 'SVG Files (*.svg)|*.svg|XML Files (*.xml)|*.xml|All Files (*.*)|*.*';
    $OpenFileDialog.DefaultExt = '.xml';
    #$OpenFileDialog.InitialDirectory = $TargetFolder;
    $OpenFileDialog.CheckFileExists = $true;
    $OpenFileDialog.Title = 'Input File';
    $OpenFileDialog.CustomPlaces.Add('C:\Users\lerwi\AndroidStudioProjects\WguScheduler356334\app\src\main\res\drawable');
    $OpenFileDialog.CustomPlaces.Add('C:\Users\lerwi\AndroidStudioProjects\WguScheduler356334\app\src\main\res\drawable-v24');
    $OpenFileDialog.CustomPlaces.Add('C:\Users\lerwi\AndroidStudioProjects');
        
    if ($OpenFileDialog.ShowDialog()) {
        $OpenFileDialog.FileName | Write-Output;
    }
}

Function Invoke-SaveFileDialog {
    [CmdletBinding()]
    Param(
        [Parameter(Mandatory = $true)]
        [String]$FileName
    )

    $SaveFileDialog = [Microsoft.Win32.SaveFileDialog]::new();
    $SaveFileDialog.AddExtension = $true;
    $SaveFileDialog.CheckPathExists = $true;
    $SaveFileDialog.CustomPlaces.Add('C:\Users\lerwi\AndroidStudioProjects\WguScheduler356334\app\src\main\res\drawable');
    $SaveFileDialog.CustomPlaces.Add('C:\Users\lerwi\AndroidStudioProjects\WguScheduler356334\app\src\main\res\drawable-v24');
    $SaveFileDialog.CustomPlaces.Add('C:\Users\lerwi\AndroidStudioProjects');
    $DefaultExt = [System.IO.Path]::GetExtension($FileName);
    if ($null -ne $DefaultExt -and $DefaultExt.Length -gt 1 -and $DefaultExt.StartsWith('.')) {
        $SaveFileDialog.DefaultExt = $DefaultExt;
        switch ($DefaultExt.ToLower()) {
            '.xml' {
                $SaveFileDialog.Filter = 'XML Files (*.xml)|*.xml|SVG Files (*.svg)|*.svg|All Files (*.*)|*.*';
                break;
            }
            '.svg' {
                $SaveFileDialog.Filter = 'SVG Files (*.svg)|*.svg|XML Files (*.xml)|*.xml|All Files (*.*)|*.*';
                break;
            }
            default {
                $SaveFileDialog.Filter = ("$($DefaultExt.Substring(1).ToUpper()) Files ($($DefaultExt.ToLower()))|*.svg|SVG Files (*.svg)|*.svg|XML Files (*.xml)|*.xml|All Files (*.*)|*.*");
                break;
            }
        }
    } else {
        $SaveFileDialog.Filter = 'XML Files (*.xml)|*.xml|SVG Files (*.svg)|*.svg|All Files (*.*)|*.*';
    }
    $SaveFileDialog.FileName = [System.IO.Path]::GetFileName($FileName);
    $SaveFileDialog.InitialDirectory = [System.IO.Path]::GetDirectoryName($FileName);
    $SaveFileDialog.OverwritePrompt = $true;
    $SaveFileDialog.RestoreDirectory = $true;
    $SaveFileDialog.Title = 'Target File';
    if ($SaveFileDialog.ShowDialog()) {
        $SaveFileDialog.FileName | Write-Output;
    }
}

Function Convert-AndroidToSvg {
    [CmdletBinding()]
    Param(
        [Parameter(Mandatory = $true, ValueFromPipeline = $true)]
        [System.Xml.XmlElement]$AndroidXml,
        [Parameter(Mandatory = $true)]
        [System.Xml.XmlNamespaceManager]$AndroidNsmgr,
        [Parameter(Mandatory = $true)]
        [System.Xml.XmlElement]$SvgXml,
        [Parameter(Mandatory = $true)]
        [System.Xml.XmlNamespaceManager]$SvgNsmgr
    )

    Begin {
        $OwnerDocument = $SvgXml.OwnerDocument;
    }

    Process {
        $AndroidAttribute = $AndroidXml.SelectSingleNode('@android:name', $AndroidNsmgr);
        
        switch -CaseSensitive ($AndroidXml.LocalName) {
            'vector' {
                if (-not [Object]::Equals($AndroidXml, $AndroidXml.OwnerDocument.DocumentElement)) {
                    Write-Error -Message "Unsupported element: $($AndroidXml.LocalName)" -Category NotImplemented;
                }
                $SvgAttribute = $SvgXml.SelectSingleNode('@id');
                if ($null -ne $AndroidAttribute) {
                    if ($null -eq $SvgAttribute) {
                        $SvgXml.Attributes.Append($OwnerDocument.CreateElement('id')).Value = $AndroidAttribute.Value;
                    } else {
                        $SvgAttribute.Value = $AndroidAttribute.Value;
                    }
                } else {
                    if ($null -ne $SvgAttribute) { $SvgXml.Attributes.Remove($SvgAttribute) | Out-Null }
                }
                $AndroidAttribute = $AndroidXml.SelectSingleNode('@android:width', $AndroidNsmgr);
                $SvgAttribute = $SvgXml.SelectSingleNode('@width');
                if ($null -ne $AndroidAttribute) {
                    if ($null -eq $SvgAttribute) {
                        $SvgXml.Attributes.Append($OwnerDocument.CreateElement('width')).Value = $AndroidAttribute.Value;
                    } else {
                        $SvgAttribute.Value = $AndroidAttribute.Value;
                    }
                } else {
                    if ($null -ne $SvgAttribute) { $SvgXml.Attributes.Remove($SvgAttribute) | Out-Null }
                }
                $AndroidAttribute = $AndroidXml.SelectSingleNode('@android:height', $AndroidNsmgr);
                $SvgAttribute = $SvgXml.SelectSingleNode('@height');
                if ($null -ne $AndroidAttribute) {
                    if ($null -eq $SvgAttribute) {
                        $SvgXml.Attributes.Append($OwnerDocument.CreateElement('height')).Value = $AndroidAttribute.Value;
                    } else {
                        $SvgAttribute.Value = $AndroidAttribute.Value;
                    }
                } else {
                    if ($null -ne $SvgAttribute) { $SvgXml.Attributes.Remove($SvgAttribute) | Out-Null }
                }
                $AndroidAttribute = $AndroidXml.SelectSingleNode('@android:viewportWidth', $AndroidNsmgr);
                $SvgAttribute = $SvgXml.SelectSingleNode('@viewBox');
                if ($null -eq $AndroidAttribute) {
                    $AndroidAttribute = $AndroidXml.SelectSingleNode('@android:viewportHeight', $AndroidNsmgr);
                    if ($null -eq $AndroidAttribute) {
                        if ($null -ne $SvgAttribute) { $SvgXml.Attributes.Remove($SvgAttribute) | Out-Null }
                    } else {
                        if ($null -eq $SvgAttribute) {
                            $SvgXml.Attributes.Append($OwnerDocument.CreateElement('height')).Value = "0 0 $($AndroidAttribute.Value) $($AndroidAttribute.Value)";
                        } else {
                            $SvgAttribute.Value = "0 0 $($AndroidAttribute.Value) $($AndroidAttribute.Value)";
                        }
                    }
                } else {
                    $viewportWidth = $AndroidAttribute.Value;
                    $AndroidAttribute = $AndroidXml.SelectSingleNode('@android:viewportHeight', $AndroidNsmgr);
                    if ($null -eq $AndroidAttribute) {
                        if ($null -eq $SvgAttribute) {
                            $SvgXml.Attributes.Append($OwnerDocument.CreateElement('viewBox')).Value = "0 0 $viewportWidth $viewportWidth";
                        } else {
                            $SvgAttribute.Value = "0 0 $viewportWidth $($AndroidAttribute.Value)";
                        }
                    } else {
                        if ($null -eq $SvgAttribute) {
                            $SvgXml.Attributes.Append($OwnerDocument.CreateElement('viewBox')).Value = "0 0 $viewportWidth $viewportWidth";
                        } else {
                            $SvgAttribute.Value = "0 0 $viewportWidth $($AndroidAttribute.Value)";
                        }
                    }
                }
                @($AndroidXml.SelectNodes('android:*', $AndroidNsmgr)) | Convert-AndroidToSvg -AndroidNsmgr $AndroidNsmgr -SvgXml $XmlElement -SvgNsmgr $SvgNsmgr;
                break;
            }
            'group' {
                $XmlElement = $SvgXml.AppendChild($OwnerDocument.CreateElement('g', $Script:XmlNs.svg));
                if ($null -ne $AndroidAttribute) {
                    $XmlElement.Attributes.Append($OwnerDocument.CreateElement('id')).Value = $AndroidAttribute.Value;
                }
                @($AndroidXml.SelectNodes('android:*', $AndroidNsmgr)) | Convert-AndroidToSvg -AndroidNsmgr $AndroidNsmgr -SvgXml $XmlElement -SvgNsmgr $SvgNsmgr;
                break;
            }
            'path' {
                $XmlElement = $SvgXml.AppendChild($OwnerDocument.CreateElement('path', $Script:XmlNs.svg));
                if ($null -ne $AndroidAttribute) {
                    $XmlElement.Attributes.Append($OwnerDocument.CreateElement('id')).Value = $AndroidAttribute.Value;
                }
                $Styles = @();
                $AndroidAttribute = $AndroidXml.SelectSingleNode('@android:fillColor', $AndroidNsmgr);
                if ($null -ne $AndroidAttribute -and $AndroidAttribute.Value.StartsWith('#')) {
                    $Hex = $AndroidAttribute.Value.Substring(1);
                    $a = '1';
                    if ($Hex.Length -gt 7) {
                        $a = 255 / [int]::Parse($Hex.Substring(0, 2), [System.Globalization.NumberStyles]::HexNumber);
                        $Hex = $Hex.Substring(2);
                    }
                    $Styles += @("fill:$Hex;fill-opacity:$a");
                }
                $AndroidAttribute = $AndroidXml.SelectSingleNode('@android:strokeColor', $AndroidNsmgr);
                if ($null -ne $AndroidAttribute -and $AndroidAttribute.Value.StartsWith('#')) {
                    $Hex = $AndroidAttribute.Value.Substring(1);
                    $a = '1';
                    if ($Hex.Length -gt 7) {
                        $a = 255 / [int]::Parse($Hex.Substring(0, 2), [System.Globalization.NumberStyles]::HexNumber);
                        $Hex = $Hex.Substring(2);
                    }
                    $Styles += @("stroke:$Hex;stroke-opacity:$a");
                }
                $AndroidAttribute = $AndroidXml.SelectSingleNode('@android:strokeWidth', $AndroidNsmgr);
                if ($null -ne $AndroidAttribute) { $Styles += @("stroke-width:$($AndroidAttribute.Value)"); }
                if ($Styles.Count -gt 0) {
                    $XmlElement.Attributes.Append($OwnerDocument.CreateElement('style')).Value = $Styles -join ';';
                }
                $AndroidAttribute = $AndroidXml.SelectSingleNode('@android:pathData', $AndroidNsmgr);
                $XmlElement.Attributes.Append($OwnerDocument.CreateElement('d')).Value = $AndroidAttribute.Value;
                break;
            }
            default {
                Write-Error -Message "Unsupported element: $($AndroidXml.LocalName)" -Category NotImplemented;
                break;
            }
        }
    }
}

Function Convert-SvgToAndroid {
    [CmdletBinding()]
    Param(
        [Parameter(Mandatory = $true, ValueFromPipeline = $true)]
        [System.Xml.XmlDocument]$SvgXml,
        [Parameter(Mandatory = $true)]
        [System.Xml.XmlNamespaceManager]$SvgNsmgr,
        [Parameter(Mandatory = $true)]
        [System.Xml.XmlDocument]$AndroidXml,
        [Parameter(Mandatory = $true)]
        [System.Xml.XmlNamespaceManager]$AndroidNsmgr
    )

    Begin {
        $OwnerDocument = $AndroidXml.OwnerDocument;
    }

    Process {
        $SvgAttribute = $SvgXml.SelectSingleNode('@id');
        
        switch -CaseSensitive ($SvgXml.LocalName) {
            'vector' {
                if (-not [Object]::Equals($SvgXml, $SvgXml.OwnerDocument.DocumentElement)) {
                    Write-Error -Message "Unsupported element: $($SvgXml.LocalName)" -Category NotImplemented;
                }
                $AndroidAttribute = $AndroidXml.SelectSingleNode('@android:name', $AndroidNsmgr);
                if ($null -ne $SvgAttribute) {
                    if ($null -eq $AndroidAttribute) {
                        $AndroidXml.Attributes.Append($OwnerDocument.CreateElement('name', $AndroidNsmgr)).Value = $SvgAttribute.Value;
                    } else {
                        $AndroidAttribute.Value = $SvgAttribute.Value;
                    }
                } else {
                    if ($null -ne $AndroidAttribute) { $AndroidXml.Attributes.Remove($AndroidAttribute) | Out-Null }
                }
                $SvgAttribute = $SvgXml.SelectSingleNode('@width');
                $AndroidAttribute = $AndroidXml.SelectSingleNode('@android:width', $AndroidNsmgr);
                if ($null -ne $SvgAttribute) {
                    if ($null -eq $AndroidAttribute) {
                        $AndroidXml.Attributes.Append($OwnerDocument.CreateElement('width', $AndroidNsmgr)).Value = $SvgAttribute.Value;
                    } else {
                        $AndroidAttribute.Value = $SvgAttribute.Value;
                    }
                } else {
                    if ($null -ne $AndroidAttribute) { $AndroidXml.Attributes.Remove($AndroidAttribute) | Out-Null }
                }
                $SvgAttribute = $SvgXml.SelectSingleNode('@height');
                $AndroidAttribute = $AndroidXml.SelectSingleNode('@android:height', $AndroidNsmgr);
                if ($null -ne $SvgAttribute) {
                    if ($null -eq $AndroidAttribute) {
                        $AndroidXml.Attributes.Append($OwnerDocument.CreateElement('height', $AndroidNsmgr)).Value = $SvgAttribute.Value;
                    } else {
                        $AndroidAttribute.Value = $SvgAttribute.Value;
                    }
                } else {
                    if ($null -ne $AndroidAttribute) { $AndroidXml.Attributes.Remove($AndroidAttribute) | Out-Null }
                }
                $SvgAttribute = $SvgXml.SelectSingleNode('@viewBox');
                $AndroidAttribute = $AndroidXml.SelectSingleNode('@android:viewportWidth', $AndroidNsmgr);
                if ($null -eq $SvgAttribute) {
                    if ($null -ne $AndroidAttribute) { $AndroidXml.Attributes.Remove($AndroidAttribute) | Out-Null }
                    $AndroidAttribute = $AndroidXml.SelectSingleNode('@android:viewportHeight', $AndroidNsmgr);
                    if ($null -ne $AndroidAttribute) { $AndroidXml.Attributes.Remove($AndroidAttribute) | Out-Null }
                } else {
                    ($x, $y, $viewportWidth, $viewportHeight) = @(($SvgAttribute.Value -split '\s+') | ForEach-Object { [int]::Parse($_) });
                    if ($null -ne $viewportWidth) {
                        if ($null -eq $AndroidAttribute) {
                            $AndroidXml.Attributes.Append($OwnerDocument.CreateElement('viewportWidth', $AndroidNsmgr)).Value = $viewportWidth;
                        } else {
                            $AndroidAttribute.Value = $viewportWidth;
                        }
                    } else {
                        if ($null -ne $AndroidAttribute) { $AndroidXml.Attributes.Remove($AndroidAttribute) | Out-Null }
                    }
                    if ($null -ne $viewportHeight) {
                        if ($null -eq $AndroidAttribute) {
                            $AndroidXml.Attributes.Append($OwnerDocument.CreateElement('viewportHeight', $AndroidNsmgr)).Value = $viewportHeight;
                        } else {
                            $AndroidAttribute.Value = $viewportHeight;
                        }
                    } else {
                        if ($null -ne $AndroidAttribute) { $AndroidXml.Attributes.Remove($AndroidAttribute) | Out-Null }
                    }
                }
                @($SvgXml.SelectNodes('svg:*', $SvgNsmgr)) | Convert-SvgToAndroid -SvgNsmgr $SvgNsmgr -AndroidXml $AndroidXml -AndroidNsmgr $AndroidNsmgr;
                break;
            }
            'g' {
                $XmlElement = $AndroidXml.AppendChild($OwnerDocument.CreateElement('group', $Script:XmlNs.android));
                if ($null -ne $SvgAttribute) {
                    $AndroidXml.Attributes.Append($OwnerDocument.CreateElement('name', $AndroidNsmgr)).Value = $SvgAttribute.Value;
                }
                @($SvgXml.SelectNodes('svg:*', $SvgNsmgr)) | Convert-SvgToAndroid -SvgNsmgr $SvgNsmgr -AndroidXml $XmlElement -AndroidNsmgr $AndroidNsmgr;
                break;
            }
            'path' {
                $XmlElement = $SvgXml.AppendChild($OwnerDocument.CreateElement('path', $Script:XmlNs.android));
                if ($null -ne $AndroidAttribute) {
                    $AndroidXml.Attributes.Append($OwnerDocument.CreateElement('name', $AndroidNsmgr)).Value = $SvgAttribute.Value;
                }
                $SvgAttribute = $SvgXml.SelectSingleNode('@style');
                $Styles = @{};
                if ($null -ne $SvgAttribute) {
                    @($SvgAttribute.Value -split ';') | ForEach-Object { $_.Trim() } | ForEach-Object {
                        $i = $_.IndexOf(':');
                        if ($i -gt 0) {
                            $Styles[$_.Substring(0, $i)] = $_.Substring($i + 1);
                        }
                    }
                    if ($Styles.ContainsKey('fill')) {
                        [byte]$b = 255;
                        if ($Styles.ContainsKey('fill-opacity')) {
                            [byte]$b = 255.0 * [double]::Parse($Styles['fill-opacity']);
                        }
                        $AndroidXml.Attributes.Append($OwnerDocument.CreateElement('fillColor', $AndroidNsmgr)).Value = "#$($b.ToString('X2'))$($Styles['fill'])";
                    }
                    if ($Styles.ContainsKey('stroke')) {
                        [byte]$b = 255;
                        if ($Styles.ContainsKey('stroke-opacity')) {
                            [byte]$b = 255.0 * [double]::Parse($Styles['stroke-opacity']);
                        }
                        $AndroidXml.Attributes.Append($OwnerDocument.CreateElement('strokeColor', $AndroidNsmgr)).Value = "#$($b.ToString('X2'))$($Styles['stroke'])";
                    }
                    if ($Styles.ContainsKey('stroke-width')) {
                        $AndroidXml.Attributes.Append($OwnerDocument.CreateElement('strokeWidth', $AndroidNsmgr)).Value = $Styles['stroke-width'];
                    }
                }
                $SvgAttribute = $SvgXml.SelectSingleNode('@d');
                $XmlElement.Attributes.Append($OwnerDocument.CreateElement('@android:pathData', $AndroidNsmgr)).Value
            }
            'metadata' {
                break;
            }
            'defs' {
                break;
            }
            default {
                Write-Error -Message "Unsupported element: $($SvgXml.LocalName)" -Category NotImplemented;
                break;
            }
        }
    }
}

$InputPath = Invoke-OpenFileDialog;
[System.Xml.XmlDocument]$OutputDocument = $null;
$OutputPath = $null;
if ($null -ne $InputPath) {
    $InputDocument = [System.Xml.XmlDocument]::new();
    $InputDocument.Load($InputPath);
    if ($null -ne $InputDocument.DocumentElement) {
        if ($InputDocument.DocumentElement.LocalName -ceq 'vector' -and $InputDocument.DocumentElement.NamespaceURI -ceq $Script:XmlNs.android) {
            $OutputPath = [System.IO.Path]::GetDirectoryName($InputPath), [System.IO.Path]::GetFileNameWithoutExtension($InputPath) + '.svg';
            $OutputDocument = [System.Xml.XmlDocument]::new();
            if ([System.IO.File]::Exists($OutputPath)) {
                $OutputDocument.Load($OutputPath);
                if ($null -eq $OutputDocument.DocumentElement) {
                    Write-Warning -Message 'No XML content loaded';
                    $OutputDocument = $null;
                } else {
                    if ($OutputDocument.DocumentElement.LocalName -ceq 'svg' -and $OutputDocument.DocumentElement.NamespaceURI -ceq $Script:XmlNs.svg) {
                        $AndroidNsmgr = [System.Xml.XmlNamespaceManager]::new($InputDocument.NameTable);
                        $AndroidNsmgr.AddNamespace('android', $Script:XmlNs['android']);
                        $SvgNsmgr = [System.Xml.XmlNamespaceManager]::new($OutputDocument.NameTable);
                        $Script:XmlNs.Keys | Where-Object { $_ -ne 'android' } | ForEach-Object { $SvgNsmgr.AddNamespace($_, $Script:XmlNs[$_]) }
                        $OutputDocument.DocumentElement.RemoveAll();
                        @($InputDocument.DocumentElement.SelectNodes('android:*', $AndroidNsmgr)) | Convert-AndroidToSvg -AndroidNsmgr $AndroidNsmgr -SvgXml $OutputDocument.DocumentElement -SvgNsmgr $SvgNsmgr -ErrorAction Stop;
                    } else {
                        Write-Warning -Message "Invalid root element in target document ($([System.Xml.XmlQualifiedName]::new($OutputDocument.DocumentElement.LocalName, $OutputDocument.DocumentElement.NamespaceURI)) != $([System.Xml.XmlQualifiedName]::new('svg', $Script:XmlNs.svg)))";
                        $OutputDocument = $null;
                    }
                }
            } else {
                $OutputDocument.LoadXml(@'
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<svg
   xmlns:dc="http://purl.org/dc/elements/1.1/"
   xmlns:cc="http://creativecommons.org/ns#"
   xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
   xmlns:svg="http://www.w3.org/2000/svg"
   xmlns="http://www.w3.org/2000/svg"
   width="24dp"
   height="24dp"
   viewBox="0 0 24 24"
   version="1.1">
  <g />
</svg>
'@);
                $AndroidNsmgr = [System.Xml.XmlNamespaceManager]::new($InputDocument.NameTable);
                $AndroidNsmgr.AddNamespace('android', $Script:XmlNs['android']);
                $SvgNsmgr = [System.Xml.XmlNamespaceManager]::new($OutputDocument.NameTable);
                $Script:XmlNs.Keys | Where-Object { $_ -ne 'android' } | ForEach-Object { $SvgNsmgr.AddNamespace($_, $Script:XmlNs[$_]) }
                $OutputDocument.DocumentElement.RemoveAll();
                @($InputDocument.DocumentElement.SelectNodes('android:*', $AndroidNsmgr)) | Convert-AndroidToSvg -AndroidNsmgr $AndroidNsmgr -SvgXml $OutputDocument.DocumentElement -SvgNsmgr $SvgNsmgr -ErrorAction Stop;
            }
        } else {
            if ($InputDocument.DocumentElement.LocalName -ceq 'svg' -and $InputDocument.DocumentElement.NamespaceURI -ceq $Script:XmlNs.svg) {
                $OutputPath = [System.IO.Path]::GetDirectoryName($InputPath), [System.IO.Path]::GetFileNameWithoutExtension($InputPath) + '.xml';
                $OutputDocument = [System.Xml.XmlDocument]::new();
                if ([System.IO.File]::Exists($OutputPath)) {
                    $OutputDocument.Load($OutputPath);
                    if ($null -eq $OutputDocument.DocumentElement) {
                        Write-Warning -Message 'No XML content loaded';
                        $OutputDocument = $null;
                    } else {
                        if ($OutputDocument.DocumentElement.LocalName -ceq 'vector' -and $OutputDocument.DocumentElement.NamespaceURI -ceq $Script:XmlNs.android) {
                            $SvgNsmgr = [System.Xml.XmlNamespaceManager]::new($InputDocument.NameTable);
                            $Script:XmlNs.Keys | Where-Object { $_ -ne 'android' } | ForEach-Object { $SvgNsmgr.AddNamespace($_, $Script:XmlNs[$_]) }
                            $AndroidNsmgr = [System.Xml.XmlNamespaceManager]::new($OutputDocument.NameTable);
                            $AndroidNsmgr.AddNamespace('android', $Script:XmlNs['android']);
                            $OutputDocument.DocumentElement.RemoveAll();
                            @($InputDocument.DocumentElement.SelectNodes('svg:*', $SvgNsmgr)) | Convert-SvgToAndroid -SvgNsmgr $SvgNsmgr -AndroidXml $OutputDocument.DocumentElement -AndroidNsmgr $AndroidNsmgr -ErrorAction Stop;
                        } else {
                            Write-Warning -Message "Invalid root element in target document ($([System.Xml.XmlQualifiedName]::new($OutputDocument.DocumentElement.LocalName, $OutputDocument.DocumentElement.NamespaceURI)) != $([System.Xml.XmlQualifiedName]::new('vector', $Script:XmlNs.android)))";
                            $OutputDocument = $null;
                        }
                    }
                } else {
                    $OutputDocument.LoadXml(@'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24.0"
    android:viewportHeight="24.0" />
'@);
                    $SvgNsmgr = [System.Xml.XmlNamespaceManager]::new($InputDocument.NameTable);
                    $Script:XmlNs.Keys | Where-Object { $_ -ne 'android' } | ForEach-Object { $SvgNsmgr.AddNamespace($_, $Script:XmlNs[$_]) }
                    $AndroidNsmgr = [System.Xml.XmlNamespaceManager]::new($OutputDocument.NameTable);
                    $AndroidNsmgr.AddNamespace('android', $Script:XmlNs['android']);
                    $OutputDocument.DocumentElement.RemoveAll();
                    @($InputDocument.DocumentElement.SelectNodes('svg:*', $SvgNsmgr)) | Convert-SvgToAndroid -SvgNsmgr $SvgNsmgr -AndroidXml $OutputDocument.DocumentElement -AndroidNsmgr $AndroidNsmgr -ErrorAction Stop;
                }
            } else {
                "Unknown root element $([System.Xml.XmlQualifiedName]::new($InputDocument.DocumentElement.LocalName, $InputDocument.DocumentElement.NamespaceURI))";
            }
        }
    }
}
