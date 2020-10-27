Function Convert-RgbToHsv {
    [CmdletBinding()]
    Param(
      [Parameter(Mandatory = $true, Position = 0)]
      [ValidatePattern("^[a-fA-F\d]{6}")]
      [string]$RGB
    )
    $Color = [System.Drawing.Color]::FromArgb([int]::Parse($RGB.Substring(0, 2), [System.Globalization.NumberStyles]::HexNumber), [int]::Parse($RGB.Substring(2, 2), [System.Globalization.NumberStyles]::HexNumber), [int]::Parse($RGB.Substring(4, 2), [System.Globalization.NumberStyles]::HexNumber));
    $Result = New-Object -TypeName 'System.Management.Automation.PSObject' -Property @{
        Red = ([float]($Color.R / 255.0));
        Green = ([float]($Color.G / 255.0));
        Blue = ([float]($Color.B / 255.0));
        RGB = $RGB;
        Hue = $Color.GetHue();
    }
    [double]$max = [Math]::Max($Color.R, [Math]::Max($Color.G, $Color.B));
    [double]$min = [Math]::Min($Color.R, [Math]::Min($Color.G, $Color.B));
    
    $hue = $color.GetHue();
    if ($max -eq 0) {
        $Result | Add-Member -MemberType NoteProperty -Name 'Saturation' -Value ([float]0);
    } else {
        $Result | Add-Member -MemberType NoteProperty -Name 'Saturation' -Value ([float](1.0 - ($min / $max)));
    }
    ($Result | Add-Member -MemberType NoteProperty -Name 'Value' -Value ([float]($max / 255.0)) -PassThru) | Add-Member -MemberType ScriptMethod -Name 'ToString' -Value {
        "{ Red=$($this.Red); Green=$($this.Green); Blue=$($this.Blue); Hue=$($this.Hue); Saturation=$($this.Saturation); Value=$($this.Value); RGB=`"$($this.RGB)`" }";
    } -Force -PassThru;
}

Function Convert-HsvToRgb {
    [CmdletBinding()]
    Param(
      [Parameter(Mandatory = $true, Position = 0)]
      [ValidateRange(0.0, 360.0)]
      [float]$H,
      
      [Parameter(Mandatory = $true, Position = 1)]
      [ValidateRange(0.0, 1.0)]
      [float]$S,
      
      [Parameter(Mandatory = $true, Position = 2)]
      [ValidateRange(0.0, 1.0)]
      [float]$V
    )
   $Result = New-Object -TypeName 'System.Management.Automation.PSObject' -Property @{
        Hue = $H;
        Saturation = $S;
        Value = $V;
    }
    $x = [Convert]::ToInt32([Math]::Floor($H / 60.0)) % 6;
    $f = ($H / 60.0) - [Math]::Floor($H / 60.0);
    $b = $v * 255;
    $m = [Convert]::ToInt32($b);
    $p = [Convert]::ToInt32($b * (1.0 - $S));
    $q = [Convert]::ToInt32($b * (1.0 - ($f * $S)));
    $t = [Convert]::ToInt32($b * (1.0 - ((1.0 - $f) * $S)));
    
    $Color = $null;
    switch ($x) {
        1 {
            $Color = [System.Drawing.Color]::FromArgb($q, $m, $p);
            break;
        }
        2 {
            $Color = [System.Drawing.Color]::FromArgb($p, $m, $t);
            break;
        }
        3 {
            $Color = [System.Drawing.Color]::FromArgb($p, $q, $m);
            break;
        }
        4 {
            $Color = [System.Drawing.Color]::FromArgb($t, $p, $m);
            break;
        }
        5 {
            $Color = [System.Drawing.Color]::FromArgb($m, $p, $q);
            break;
        }
        default {
            $Color = [System.Drawing.Color]::FromArgb($m, $t, $p);
            break;
        }
    }
    $Result | Add-Member -MemberType NoteProperty -Name 'Red' -Value ([float]([Convert]::ToSingle($Color.R) / 255.0));
    $Result | Add-Member -MemberType NoteProperty -Name 'Green' -Value ([float]([Convert]::ToSingle($Color.G) / 255.0));
    $Result | Add-Member -MemberType NoteProperty -Name 'Blue' -Value ([float]([Convert]::ToSingle($Color.B) / 255.0));
    ($Result | Add-Member -MemberType NoteProperty -Name 'RGB' -Value $Color.ToArgb().ToString('x8').Substring(2) -PassThru) | Add-Member -MemberType ScriptMethod -Name 'ToString' -Value {
        "{ Red=$($this.Red); Green=$($this.Green); Blue=$($this.Blue); Hue=$($this.Hue); Saturation=$($this.Saturation); Value=$($this.Value); RGB=`"$($this.RGB)`" }";
    } -Force -PassThru;
}

$NormalColor = Convert-RgbToHsv -RGB "B71C1C";
if ($NormalColor.Hue -lt 180.0) {
    $Hue = 180.0 + $NormalColor.Hue;
} else {
    $Hue = $NormalColor.Hue - 180.0;
}

$InverseColor = $NormalColor;
if ($NormalColor.Value -lt 0.5) {
    $Value = 1.0 - ($NormalColor.Value * 0.25);
    $InverseColor = Convert-HsvToRgb -H $Hue -S ($NormalColor.Saturation * 0.5) -V $Value;
} else {
    $Value = (1.0 - $NormalColor.Value) * 0.125;
    $InverseColor = Convert-HsvToRgb -H $Hue -S ($NormalColor.Saturation + ((1.0 - $NormalColor.Saturation) * 0.25)) -V $Value;
}

$Colors = @{
    Primary = @{
        Enabled = @{
            Normal = $NormalColor;
        };
        Disabled = @{
            Normal = Convert-HsvToRgb -H $NormalColor.Hue -S ($InverseColor.Saturation * 0.25) -V $InverseColor.Value;
        };
    };
    Inverse = @{
        Enabled = @{
            Normal = $InverseColor;
        };
        Disabled = @{
            Normal = Convert-HsvToRgb -H $InverseColor.Hue -S ($NormalColor.Saturation * 0.25) -V $InverseColor.Value;
        };
    };
};

$Colors.Keys | ForEach-Object {
    $PrimaryOrInverse = $Colors[$_];
    $PrimaryOrInverse.Keys | ForEach-Object {
        $EnabledOrDisabled = $PrimaryOrInverse[$_];
        $Hash = $EnabledOrDisabled.Normal;
        $EnabledOrDisabled['Light'] = Convert-HsvToRgb -H $Hash.Hue -S ($Hash.Saturation * 0.75) -V ($Hash.Value + ((1.0 - $Hash.Value) * 0.875));
        $EnabledOrDisabled['Dark'] = Convert-HsvToRgb -H $Hash.Hue -S ($Hash.Saturation + ((1.0 - $Hash.Saturation) * 0.75)) -V ($Hash.Value * 0.5);
    }
}

foreach ($t in @(@{ Key='Primary'; Name='' }, @{ Key='Inverse'; Name='_inverse' })) {
    $h1 = $Colors[$t.Key];
    foreach ($e in @(@{ Key='Enabled'; Name='' }, @{ Key='Disabled'; Name='_disabled' })) {
        $h2 = $h1[$e.Key];
        foreach ($v in @(@{ Key='Normal'; Name='' }, @{ Key='Dark'; Name='_dark' }, @{ Key='Light'; Name='_light' })) {
            $h3 = $h2[$v.Key];
            "color$($t.Name)$($e.Name)$($v.Name)=$($h3.RGB) (H = $($h3.Hue); S = $($h3.Saturation); V  = $($h3.Value))";
        }
    }
}

<#
$LightV = $v + (100.0 - $v) / 1.25;
$DarkV = $v / 1.25;
$LightS = $s / 2.0;
$DarkS = $s + ((100.0 - $s) / 2.0);
$DisabledS = $s / 5.0;

$ILightV = $InverseV + (100.0 - $InverseV) / 1.25;
$IDarkV = $InverseV / 1.25;
$ILightS = $InverseS / 2.0;
$IDarkS = $InverseS + ((100.0 - $InverseS) / 2.0);
$IDisabledS = $InverseS / 5.0;
#>