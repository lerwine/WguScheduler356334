$Script:TestText = @(
    @('Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.', 'Posuere ac ut consequat semper viverra.', 'Mauris rhoncus aenean vel elit scelerisque mauris pellentesque.', 'Libero justo laoreet sit amet cursus.', 'Tortor id aliquet lectus proin nibh nisl condimentum.', 'Euismod elementum nisi quis eleifend quam.', 'Scelerisque mauris pellentesque pulvinar pellentesque habitant morbi tristique senectus.', 'Turpis tincidunt id aliquet risus.', 'Placerat duis ultricies lacus sed turpis.', 'Fermentum posuere urna nec tincidunt.', 'Montes nascetur ridiculus mus mauris vitae ultricies.', 'Libero volutpat sed cras ornare arcu dui vivamus arcu.', 'Viverra vitae congue eu consequat.', 'Lectus urna duis convallis convallis.', 'Sit amet risus nullam eget felis eget nunc lobortis mattis.', 'Nulla posuere sollicitudin aliquam ultrices sagittis orci a scelerisque purus.', 'In eu mi bibendum neque egestas.'),
    @('Aenean euismod elementum nisi quis eleifend quam.', 'Egestas egestas fringilla phasellus faucibus scelerisque eleifend.', 'Diam in arcu cursus euismod quis.', 'Odio euismod lacinia at quis risus sed.', 'Malesuada fames ac turpis egestas maecenas.', 'Purus faucibus ornare suspendisse sed nisi.', 'Elit sed vulputate mi sit amet mauris commodo quis imperdiet.', 'Nunc mi ipsum faucibus vitae aliquet.', 'Eget egestas purus viverra accumsan in.', 'Suscipit tellus mauris a diam maecenas sed enim ut.', 'Aliquet risus feugiat in ante metus dictum at tempor commodo.', 'Id semper risus in hendrerit.', 'Tristique senectus et netus et malesuada fames ac turpis.', 'Sit amet justo donec enim diam vulputate ut pharetra.', 'Gravida cum sociis natoque penatibus et.', 'Fringilla urna porttitor rhoncus dolor purus non enim praesent elementum.', 'Aliquet sagittis id consectetur purus ut.', 'Dui id ornare arcu odio.'),
    @('Volutpat commodo sed egestas egestas fringilla.', 'Egestas purus viverra accumsan in.', 'Nisi vitae suscipit tellus mauris a diam maecenas sed.', 'Aenean vel elit scelerisque mauris pellentesque pulvinar pellentesque habitant.', 'Leo a diam sollicitudin tempor id.', 'Arcu risus quis varius quam quisque.', 'Ridiculus mus mauris vitae ultricies leo integer malesuada nunc.', 'Sit amet dictum sit amet justo donec.', 'Nam libero justo laoreet sit amet cursus sit.', 'Risus feugiat in ante metus dictum at.'),
    @('Dignissim diam quis enim lobortis scelerisque fermentum dui faucibus in.', 'Mattis aliquam faucibus purus in massa tempor nec.', 'Felis bibendum ut tristique et egestas.', 'Eget mauris pharetra et ultrices neque ornare aenean.', 'Risus quis varius quam quisque id diam.', 'In nulla posuere sollicitudin aliquam ultrices sagittis.', 'Amet tellus cras adipiscing enim eu.', 'Lacus suspendisse faucibus interdum posuere lorem ipsum dolor.', 'Eu feugiat pretium nibh ipsum.', 'Venenatis cras sed felis eget velit aliquet sagittis.', 'Ut venenatis tellus in metus vulputate eu scelerisque felis imperdiet.', 'Quis hendrerit dolor magna eget.', 'Quis hendrerit dolor magna eget est lorem ipsum.', 'Donec et odio pellentesque diam.', 'Amet aliquam id diam maecenas ultricies mi eget mauris.'),
    @('Praesent tristique magna sit amet purus gravida quis blandit turpis.', 'Magnis dis parturient montes nascetur ridiculus.', 'Quis commodo odio aenean sed adipiscing.', 'Nulla facilisi morbi tempus iaculis urna id volutpat lacus.', 'Turpis egestas integer eget aliquet nibh praesent tristique.', 'Nisl rhoncus mattis rhoncus urna neque.', 'Proin fermentum leo vel orci porta non pulvinar neque laoreet.', 'Ut enim blandit volutpat maecenas.', 'In ornare quam viverra orci sagittis eu.', 'Maecenas ultricies mi eget mauris pharetra et ultrices neque ornare.', 'Ut diam quam nulla porttitor massa id neque.', 'Enim neque volutpat ac tincidunt.', 'Arcu cursus euismod quis viverra nibh cras pulvinar mattis.', 'Habitasse platea dictumst quisque sagittis purus sit.')
);
$Script:LastTextLine = $Script:TestText.Count - 1;
$Script:Path = 'C:\Users\lerwi\AndroidStudioProjects\WguScheduler356334\app\src\main\res\xml\sample_data.xml';
$Script:XmlDocument = [System.Xml.XmlDocument]::new();
$Script:Random = [Random]::new();

Function Set-RandomNoteText {
    Param(
        [Parameter(Mandatory = $true)]
        [System.Xml.XmlElement]$ParentElement
    )
    
    $NotesElement = $ParentElement.SelectSingleNode('notes');
    $StartIndex = $Random.Next(-1, $Script:TestText.Count);
    if ($StartIndex -lt 0) {
        if ($null -ne $NotesElement) {
            $ParentElement.RemoveChild($NotesElement) | Out-Null;
            if ($null -eq $ParentElement.SelectSingleNode('*')) {
                $ParentElement.IsEmpty = true;
            }
        }
    } else {
        if ($null -eq $NotesElement) {
            $FirstElement = $ParentElement.SelectSingleNode('child::*[1]');
            if ($null -eq $FirstElement) {
                $NotesElement = $ParentElement.AppendChild($ParentElement.OwnerDocument.CreateElement('notes'));
            } else {
                $NotesElement = $ParentElement.InsertBefore($ParentElement.OwnerDocument.CreateElement('notes'), $FirstElement);
            }
        } else {
            $NotesElement.RemoveAll() | Out-Null;
        }
        $EndInclIndex = $StartIndex;
        if ($StartIndex -lt $Script:LastTextLine) {
            $TryCount = $Random.Next(0, $Script:TestText.Count);
            for ($i = 0; $i -lt $TryCount; $i++) {
                if ($Random.Next(0, 4) -eq 0 -or ++$EndInclIndex -eq $Script:LastTextLine) { break; }
            }
        }
        $Paragraphs = $Script:TestText;
        if ($StartIndex -eq $EndInclIndex) {
            $Paragraphs = @(,$Script:TestText[$StartIndex]);
        } else {
            if ($StartIndex -gt 0 -or $EndInclIndex -lt $Script:LastTextLine) {
                $Paragraphs = $Script:TestText[$StartIndex..$EndInclIndex];
            }
        }

        $Lines = @();
        for ($i = 0; $i -lt $Paragraphs.Count; $i++) {
            $StringBuilder = [System.Text.StringBuilder]::new();
            $Sentences = $Paragraphs[$i];
            $StartIndex = $Random.Next(0, $Sentences.Count);
            $LastIndex = $Sentences.Count - 1;
            $EndInclIndex = $StartIndex;
            if ($StartIndex -lt $LastIndex) {
                $TryCount = $Random.Next(0, $Sentences.Count);
                for ($i = 0; $i -lt $TryCount; $i++) {
                    if ($Random.Next(0, 4) -eq 0 -or ++$EndInclIndex -eq $LastIndex) { break; }
                }
            }
            if ($StartIndex -eq $EndInclIndex) {
                $Lines += @($Sentences[$StartIndex]);
            } else {
                if ($StartIndex -gt 0 -or $EndInclIndex -lt $Script:LastTextLine) {
                    $Sentences = $Sentences[$StartIndex..$EndInclIndex];
                }
                $Lines += @($Sentences -join ' ');
            }
        }
        $Text = $Lines -join "`r`n\n";
        $NotesElement.AppendChild($XmlDocument.CreateCDataSection($Text)) | Out-Null;
    }
}

Function ConvertFrom-DateString {
    Param(
        [Parameter(Mandatory = $true)]
        [AllowNull()]
        [AllowEmptyString()]
        [string]$Text,
        
        [AllowNull()]
        [DateTime]$expectedStart,
        
        [AllowNull()]
        [DateTime]$actualStart,
        
        [AllowNull()]
        [DateTime]$expectedEnd,
        
        [AllowNull()]
        [DateTime]$actualEnd
    )

    if ($null -ne $Text -and ($Text = $Text.Trim()).Length -gt 0) {
        $Value = $null;
        switch ($Text) {
            'expectedStart' {
                $Value = $expectedStart;
                break;
            }
            'actualStart' {
                $Value = $actualStart;
                break;
            }
            'expectedEnd' {
                $Value = $expectedEnd;
                break;
            }
            'actualEnd' {
                $Value = $actualEnd;
                break;
            }
            default {
                $Value = [DateTime]::Parse($Text);
                break;
            }
        }
        if ($null -ne $Value) {
            $Value | Write-Output;
        }
    }
}

$Script:XmlDocument.Load($Script:Path);
foreach ($TermsElement in @($Script:XmlDocument.DocumentElement.SelectNodes('terms/item'))) {
    $TermsElement.name;
    Set-RandomNoteText -ParentElement $TermsElement;
    foreach ($CourseElement in $TermsElement.SelectNodes('courses/item')) {
        Set-RandomNoteText -ParentElement $CourseElement;
        $Text = ('' + $CourseElement.expectedStart).Trim();
        $expectedStart = ConvertFrom-DateString -Text $CourseElement.expectedStart;
        $actualStart = ConvertFrom-DateString -Text $CourseElement.actualStart;
        $eventStart = $actualStart;
        if ($null -eq $eventStart) { $eventStart = $expectedStart }
        $expectedEnd = ConvertFrom-DateString -Text $CourseElement.expectedEnd;
        $actualEnd = ConvertFrom-DateString -Text $CourseElement.actualEnd;
        $eventEnd = $actualEnd;
        if ($null -eq $eventEnd) { $eventEnd = $expectedEnd }
        $courseAlerts = $CourseElement.SelectSingleNode('courseAlerts');
        $Options = 'none';
        if ($null -eq $eventStart) {
            if ($null -ne $eventEnd) {
                $Options = 'eventEnd';
            }
        } else {
            if ($null -eq $eventEnd) {
                $Options = 'eventStart';
            } else {
                $Options = 'eventStart,eventEnd';
            }
        }
        $AlertTypes = @();
        switch ($Options) {
            'none' {
                break;
            }
            'eventStart' {
                $TryCount = $Random.Next(0, 4);
                for ($i = 0; $i -lt $TryCount; $i++) {
                    if ($Random.Next(0, 7) -eq 0) { break; }
                    $AlertTypes += @('false');
                }
                break;
            }
            'eventEnd' {
                $TryCount = $Random.Next(0, 4);
                for ($i = 0; $i -lt $TryCount; $i++) {
                    if ($Random.Next(0, 7) -eq 0) { break; }
                    $AlertTypes += @('true');
                }
                break;
            }
            default {
                $TryCount = $Random.Next(0, 4);
                for ($i = 0; $i -lt $TryCount; $i++) {
                    if ($Random.Next(0, 7) -eq 0) { break; }
                    $AlertTypes += @('false');
                }
                $TryCount = $Random.Next(0, 4);
                for ($i = 0; $i -lt $TryCount; $i++) {
                    if ($Random.Next(0, 7) -eq 0) { break; }
                    $AlertTypes += @('true');
                }
                break;
            }
        }
        if ($AlertTypes.Count -eq 0) {
            if ($null -ne $courseAlerts) {
                $CourseElement.RemoveChild($courseAlerts) | Out-Null;
                if ($null -eq $CourseElement.SelectSingleNode('*')) {
                    $CourseElement.IsEmpty = true;
                }
                $courseAlerts = $null;
            }
        } else {
            if ($null -eq $courseAlerts) {
                $FirstElement = $CourseElement.SelectSingleNode('child::*[1]');
                if ($null -eq $FirstElement) {
                    $courseAlerts = $CourseElement.AppendChild($Script:XmlDocument.CreateElement('courseAlerts'));
                } else {
                    if ($FirstElement.LocalName -eq 'notes') {
                        $courseAlerts = $CourseElement.InsertAfter($Script:XmlDocument.CreateElement('courseAlerts'), $FirstElement);
                    } else {
                        $courseAlerts = $CourseElement.InsertBefore($Script:XmlDocument.CreateElement('courseAlerts'), $FirstElement);
                    }
                }
            } else {
                $courseAlerts.removeAll();
            }
            $AlertTypes | ForEach-Object {
                $ItemElement = $courseAlerts.AppendChild($Script:XmlDocument.CreateElement('item'));
                $ItemElement.Attributes.Append($Script:XmlDocument.CreateAttribute('subsequent')).Value = $_;
                $ItemElement.Attributes.Append($Script:XmlDocument.CreateAttribute('leadTime')).Value = $Random.Next(0, 2 + ($Random.Next(0, 7) * 2));
            }
        }
        foreach ($assessmentsElement in $TermsElement.SelectNodes('assessments/item')) {
            Set-RandomNoteText -ParentElement $assessmentsElement;
            $goalDate = ConvertFrom-DateString -Text $assessmentsElement.goalDate;
            $completionDate = ConvertFrom-DateString -Text $assessmentsElement.completionDate;
            $assessmentAlerts = $assessmentsElement.SelectSingleNode('assessmentAlerts');
            $AlertTypes = @();
            if ($null -ne $goalDate) {
                $TryCount = $Random.Next(0, 4);
                for ($i = 0; $i -lt $TryCount; $i++) {
                    if ($Random.Next(0, 7) -eq 0) { break; }
                    $AlertTypes += @('false');
                }
            }
            if ($null -ne $completionDate) {
                $TryCount = $Random.Next(0, 4);
                for ($i = 0; $i -lt $TryCount; $i++) {
                    if ($Random.Next(0, 7) -eq 0) { break; }
                    $AlertTypes += @('true');
                }
            }
            if ($AlertTypes.Count -eq 0) {
                if ($null -ne $assessmentAlerts) {
                    $assessmentsElement.RemoveChild($assessmentAlerts) | Out-Null;
                    if ($null -eq $assessmentsElement.SelectSingleNode('*')) {
                        $assessmentsElement.IsEmpty = true;
                    }
                    $assessmentAlerts = $null;
                }
            } else {
                if ($null -eq $courseAlerts) {
                    $FirstElement = $assessmentsElement.SelectSingleNode('child::*[1]');
                    if ($null -eq $FirstElement) {
                        $assessmentAlerts = $assessmentsElement.AppendChild($Script:XmlDocument.CreateElement('courseAlerts'));
                    } else {
                        if ($FirstElement.LocalName -eq 'notes') {
                            $assessmentAlerts = $assessmentsElement.InsertAfter($Script:XmlDocument.CreateElement('courseAlerts'), $FirstElement);
                        } else {
                            $assessmentAlerts = $assessmentsElement.InsertBefore($Script:XmlDocument.CreateElement('courseAlerts'), $FirstElement);
                        }
                    }
                } else {
                    $assessmentAlerts.removeAll();
                }
                $AlertTypes | ForEach-Object {
                    $ItemElement = $assessmentAlerts.AppendChild($Script:XmlDocument.CreateElement('item'));
                    $ItemElement.Attributes.Append($Script:XmlDocument.CreateAttribute('subsequent')).Value = $_;
                    $ItemElement.Attributes.Append($Script:XmlDocument.CreateAttribute('leadTime')).Value = $Random.Next(0, 2 + ($Random.Next(0, 7) * 2));
                }
            }
        }
    }
}
$XmlWriterSettings = [System.Xml.XmlWriterSettings]::new();
$XmlWriterSettings.Indent = $true;
$XmlWriterSettings.IndentChars = '    ';
$XmlWriterSettings.NewLineChars = "`r`n";
$XmlWriterSettings.Encoding = [System.Text.UTF8Encoding]::new($false, $false);
$XmlWriter = [System.Xml.XmlWriter]::Create($Script:Path, $XmlWriterSettings);
try {
    $Script:XmlDocument.WriteTo($XmlWriter);
    $XmlWriter.Flush();
    $XmlWriter.Close();
} finally {
    $XmlWriter.Dispose();
}
