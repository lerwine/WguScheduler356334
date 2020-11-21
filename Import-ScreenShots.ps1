Function Create-NsMgr {
    [CmdletBinding()]
    Param(
        [Parameter(Mandatory = $true)]
        [System.Xml.XmlDocument]$Document
    )
    $Nsmgr = New-Object -TypeName 'System.Xml.XmlNamespaceManager' -ArgumentList $Document.NameTable;
    $Nsmgr.AddNamespace('dc', 'http://purl.org/dc/elements/1.1/') | Out-Null;
    $Nsmgr.AddNamespace('cc', 'http://creativecommons.org/ns#') | Out-Null;
    $Nsmgr.AddNamespace('rdf', 'http://www.w3.org/1999/02/22-rdf-syntax-ns#') | Out-Null;
    $Nsmgr.AddNamespace('svg', 'http://www.w3.org/2000/svg') | Out-Null;
    $Nsmgr.AddNamespace('xlink', 'http://www.w3.org/1999/xlink') | Out-Null;
    $Nsmgr.AddNamespace('sodipodi', 'http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd') | Out-Null;
    $Nsmgr.AddNamespace('inkscape', 'http://www.inkscape.org/namespaces/inkscape') | Out-Null;
    return ,$Nsmgr;
}

Function Get-ScreenRectElement {
    [CmdletBinding()]
    Param(
        [Parameter(Mandatory = $true)]
        [System.Xml.XmlDocument]$Document
    )
    $Nsmgr = Create-NsMgr -Document $Document -ErrorAction Stop;
    return $Document.DocumentElement.SelectSingleNode('svg:rect[@id="screenRect"]', $Nsmgr);
}

Function Import-Template {
    [CmdletBinding()]
    Param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    $Document = New-Object -TypeName 'System.Xml.XmlDocument';
    $Document.Load($Path);
    ($x1, $y1, $x2, $y2) = $Document.DocumentElement.SelectSingleNode('@viewBox').Value -split '\s+';
    $XmlElement = Get-ScreenRectElement -Document $Document -ErrorAction Stop;
    New-Object -TypeName 'System.Management.Automation.PSObject' -Property @{
        Document = $Document;
        ScreenWidth = [Convert]::ToInt32([Math]::Round([System.Xml.XmlConvert]::ToDouble($XmlElement.SelectSingleNode('@width').Value) * [System.Xml.XmlConvert]::ToDouble($Document.DocumentElement.SelectSingleNode('@width').Value) `
            / ([System.Xml.XmlConvert]::ToDouble($x2) - [System.Xml.XmlConvert]::ToDouble($x1))));
        ScreenHeight = [Convert]::ToInt32([Math]::Round([System.Xml.XmlConvert]::ToDouble($XmlElement.SelectSingleNode('@height').Value) * [System.Xml.XmlConvert]::ToDouble($Document.DocumentElement.SelectSingleNode('@height').Value) `
            / ([System.Xml.XmlConvert]::ToDouble($y2) - [System.Xml.XmlConvert]::ToDouble($y1))));
    };
}

Function Import-ScreenShot {
    [CmdletBinding()]
    Param(
        [Parameter(Mandatory = $true, ValueFromPipeline = $true)]
        [System.IO.FileInfo]$FileInfo,
        
        [Parameter(Mandatory = $true)]
        [string]$OutputPath
    )

    Begin {
        if ($null -eq $Script:__Import_ScreenShot_PortraitTemplate) {
            $Script:__Import_ScreenShot_PortraitTemplate = Import-Template -Path ($OutputPath | Join-Path -ChildPath 'CellPhonePortrait.svg') -ErrorAction Stop;
            $Script:__Import_ScreenShot_LandscapeTemplate = Import-Template -Path ($OutputPath | Join-Path -ChildPath 'CellPhoneLandscape.svg') -ErrorAction Stop;
        }
    }

    Process {
        $Document = New-Object -TypeName 'System.Xml.XmlDocument';
        Write-Information -MessageData "Reading $Path";
        $MemoryStream = [System.IO.MemoryStream]::new([System.IO.File]::ReadAllBytes($FileInfo.FullName));
        $Template = $Script:__Import_ScreenShot_PortraitTemplate;
        $Suffix = "_Portrait";
        try {
            [System.Drawing.Image]$Image = [System.Drawing.Image]::FromStream($MemoryStream);
            try {
                if ($Image.Width -gt $Image.Height) {
                    $Template = $Script:__Import_ScreenShot_LandscapeTemplate;
                    $Suffix = "_Landscape";
                }
            } finally {
                $Image.Dispose();
            }
            $Document.AppendChild($Document.ImportNode($Template.Document.DocumentElement, $true)) | Out-Null;
            $SourceElement = Get-ScreenRectElement -Document $Document -ErrorAction Stop;
            $TargetElement = $Document.DocumentElement.InsertBefore($Document.CreateElement('image', 'http://www.w3.org/2000/svg'), $SourceElement);
            $Document.DocumentElement.RemoveChild($SourceElement) | Out-Null;
            $TargetElement.Attributes.Append($Document.CreateAttribute('id')).Value = 'screenShot';
            $TargetElement.Attributes.Append($Document.CreateAttribute('href', 'http://www.w3.org/1999/xlink')).Value = 'data:image/png;base64,' + [Convert]::ToBase64String($MemoryStream.ToArray(), [System.Base64FormattingOptions]::InsertLineBreaks);
            $TargetElement.Attributes.Append($Document.CreateAttribute('preserveAspectRatio')).Value = 'none';
            ('height', 'width', 'x', 'y', 'transform') | ForEach-Object {
                $TargetElement.Attributes.Append($Document.CreateAttribute($_)).Value = $SourceElement.SelectSingleNode("@$_").Value;
            }
        } finally {
            $MemoryStream.Dispose();
        }

        $OutputFilePath = [System.IO.Path]::Combine($OutputPath, ($FileInfo.BaseName + $Suffix + '.svg'));
        $XmlWriterSettings = New-Object -TypeName 'System.Xml.XmlWriterSettings';
        $XmlWriterSettings.Indent = $true;
        $XmlWriterSettings.OmitXmlDeclaration = $true;
        $XmlWriterSettings.Encoding = New-Object -TypeName 'System.Text.UTF8Encoding' -ArgumentList $false, $false;
        Write-Information -MessageData "Writing $OutputFilePath";
        $XmlWriter = [System.Xml.XmlWriter]::Create($OutputFilePath, $XmlWriterSettings);
        try {
            $Document.WriteTo($XmlWriter);
            $XmlWriter.Flush();
            $XmlWriter.Close();
        } finally {
            $XmlWriter.Dispose();
        }
    }
}

$OutputPath = $PSScriptRoot | Join-Path -ChildPath 'Svg'
(Get-ChildItem -Path ($PSScriptRoot | Join-Path -ChildPath 'ScreenShots') -Filter '*.png') | Import-ScreenShot -OutputPath $OutputPath -ErrorAction Stop;
