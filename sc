-- South Bronx: The Trenches | Silent Aim (Anti-Lag / Optimized)
-- Dapur: ollie's bench | absolute ollie

local Players = game:GetService("Players")
local RunService = game:GetService("RunService")
local UserInputService = game:GetService("UserInputService")
local Workspace = game:GetService("Workspace")
local CoreGui = game:GetService("CoreGui")

local LocalPlayer = Players.LocalPlayer
local Camera = Workspace.CurrentCamera

local Config = {
    Enabled = true,
    AimMode = "Mouse", -- "Mouse" atau "Crosshair"
    FOV = 150,
    ShowFOV = true,
    ShowLine = true,
    WallCheck = true,
    TeamCheck = false,
    HitPart = "Head",
    Minimized = false,
    ScanRate = 0.05 -- Scan target setiap 0.05 detik (20Hz) agar FPS tetap tinggi
}

-- Hapus UI & Drawing lama jika di-execute ulang
if getgenv().OllieCleanup then
    pcall(getgenv().OllieCleanup)
end

-- ScreenGui Setup
local ScreenGui = Instance.new("ScreenGui")
ScreenGui.Name = "OllieSouthBronxUI"
ScreenGui.ResetOnSpawn = false
ScreenGui.ZIndexBehavior = Enum.ZIndexBehavior.Sibling

pcall(function()
    ScreenGui.Parent = CoreGui
end)
if not ScreenGui.Parent then
    ScreenGui.Parent = LocalPlayer:WaitForChild("PlayerGui")
end

-- Main Frame
local MainFrame = Instance.new("Frame")
MainFrame.Name = "MainFrame"
MainFrame.Size = UDim2.new(0, 250, 0, 230)
MainFrame.Position = UDim2.new(0.05, 0, 0.25, 0)
MainFrame.BackgroundColor3 = Color3.fromRGB(20, 20, 26)
MainFrame.BorderSizePixel = 0
MainFrame.Active = true
MainFrame.Draggable = true
MainFrame.Parent = ScreenGui

local MainCorner = Instance.new("UICorner")
MainCorner.CornerRadius = UDim.new(0, 8)
MainCorner.Parent = MainFrame

local MainStroke = Instance.new("UIStroke")
MainStroke.Color = Color3.fromRGB(255, 60, 140)
MainStroke.Thickness = 1.5
MainStroke.Parent = MainFrame

-- Top Bar
local TopBar = Instance.new("Frame")
TopBar.Size = UDim2.new(1, 0, 0, 34)
TopBar.BackgroundColor3 = Color3.fromRGB(30, 30, 40)
TopBar.BorderSizePixel = 0
TopBar.Parent = MainFrame

local TopCorner = Instance.new("UICorner")
TopCorner.CornerRadius = UDim.new(0, 8)
TopCorner.Parent = TopBar

local TitleLabel = Instance.new("TextLabel")
TitleLabel.Size = UDim2.new(1, -44, 1, 0)
TitleLabel.Position = UDim2.new(0, 10, 0, 0)
TitleLabel.BackgroundTransparency = 1
TitleLabel.Text = "SOUTH BRONX // LITE"
TitleLabel.TextColor3 = Color3.fromRGB(255, 255, 255)
TitleLabel.TextSize = 13
TitleLabel.Font = Enum.Font.GothamBold
TitleLabel.TextXAlignment = Enum.TextXAlignment.Left
TitleLabel.Parent = TopBar

local MinButton = Instance.new("TextButton")
MinButton.Size = UDim2.new(0, 26, 0, 24)
MinButton.Position = UDim2.new(1, -32, 0.5, -12)
MinButton.BackgroundColor3 = Color3.fromRGB(45, 45, 60)
MinButton.Text = "-"
MinButton.TextColor3 = Color3.fromRGB(255, 255, 255)
MinButton.TextSize = 16
MinButton.Font = Enum.Font.GothamBold
MinButton.Parent = TopBar

local MinCorner = Instance.new("UICorner")
MinCorner.CornerRadius = UDim.new(0, 6)
MinCorner.Parent = MinButton

-- Content Container
local ContentFrame = Instance.new("Frame")
ContentFrame.Size = UDim2.new(1, -20, 1, -44)
ContentFrame.Position = UDim2.new(0, 10, 0, 40)
ContentFrame.BackgroundTransparency = 1
ContentFrame.Parent = MainFrame

local UIList = Instance.new("UIListLayout")
UIList.Padding = UDim.new(0, 7)
UIList.SortOrder = Enum.SortOrder.LayoutOrder
UIList.Parent = ContentFrame

local function CreateButton(text, order, callback)
    local Btn = Instance.new("TextButton")
    Btn.Size = UDim2.new(1, 0, 0, 30)
    Btn.LayoutOrder = order
    Btn.BackgroundColor3 = Color3.fromRGB(35, 35, 48)
    Btn.Text = text
    Btn.TextColor3 = Color3.fromRGB(240, 240, 245)
    Btn.TextSize = 12
    Btn.Font = Enum.Font.GothamSemibold
    Btn.Parent = ContentFrame

    local Corner = Instance.new("UICorner")
    Corner.CornerRadius = UDim.new(0, 6)
    Corner.Parent = Btn

    Btn.MouseButton1Click:Connect(function()
        callback(Btn)
    end)
    return Btn
end

MinButton.MouseButton1Click:Connect(function()
    Config.Minimized = not Config.Minimized
    ContentFrame.Visible = not Config.Minimized
    MainFrame.Size = Config.Minimized and UDim2.new(0, 250, 0, 34) or UDim2.new(0, 250, 0, 230)
    MinButton.Text = Config.Minimized and "+" or "-"
end)

CreateButton("Silent Aim: ON", 1, function(btn)
    Config.Enabled = not Config.Enabled
    btn.Text = "Silent Aim: " .. (Config.Enabled and "ON" or "OFF")
    btn.BackgroundColor3 = Config.Enabled and Color3.fromRGB(35, 90, 60) or Color3.fromRGB(90, 35, 45)
end)

CreateButton("Aim Mode: Aim Follow Mouse", 2, function(btn)
    if Config.AimMode == "Mouse" then
        Config.AimMode = "Crosshair"
        btn.Text = "Aim Mode: Aim Follow Crosshair"
    else
        Config.AimMode = "Mouse"
        btn.Text = "Aim Mode: Aim Follow Mouse"
    end
end)

CreateButton("FOV Circle: ON", 3, function(btn)
    Config.ShowFOV = not Config.ShowFOV
    btn.Text = "FOV Circle: " .. (Config.ShowFOV and "ON" or "OFF")
end)

CreateButton("Target Line: ON", 4, function(btn)
    Config.ShowLine = not Config.ShowLine
    btn.Text = "Target Line: " .. (Config.ShowLine and "ON" or "OFF")
end)

-- Drawing Objects
local FOVCircle = Drawing.new("Circle")
FOVCircle.Thickness = 1.5
FOVCircle.NumSides = 36 -- Dikurangi dari 64 ke 36 agar lebih ringan dirender
FOVCircle.Radius = Config.FOV
FOVCircle.Filled = false
FOVCircle.Visible = Config.ShowFOV
FOVCircle.ZIndex = 999
FOVCircle.Transparency = 0.85
FOVCircle.Color = Color3.fromRGB(255, 60, 140)

local TargetLine = Drawing.new("Line")
TargetLine.Thickness = 1.5
TargetLine.Transparency = 0.9
TargetLine.Visible = false
TargetLine.ZIndex = 999
TargetLine.Color = Color3.fromRGB(0, 255, 170)

-- Pre-allocate RaycastParams sekali saja (hemat memori & CPU)
local SharedRayParams = RaycastParams.new()
SharedRayParams.FilterType = Enum.RaycastFilterType.Exclude
SharedRayParams.IgnoreWater = true

local function GetReferencePoint()
    if Config.AimMode == "Crosshair" then
        local Viewport = Camera.ViewportSize
        return Vector2.new(Viewport.X * 0.5, Viewport.Y * 0.5)
    end
    return UserInputService:GetMouseLocation()
end

local function IsVisible(TargetPart, Character)
    if not Config.WallCheck then return true end
    local Origin = Camera.CFrame.Position
    SharedRayParams.FilterDescendantsInstances = {LocalPlayer.Character, Camera}
    local Result = Workspace:Raycast(Origin, TargetPart.Position - Origin, SharedRayParams)
    return not Result or Result.Instance:IsDescendantOf(Character)
end

-- Variabel Cache Target
local CachedTarget = nil
local LastScan = 0

local function UpdateTargetCache(RefPoint)
    local ClosestPart = nil
    local ShortestDist = Config.FOV
    local AllPlayers = Players:GetPlayers()

    for i = 1, #AllPlayers do
        local Player = AllPlayers[i]
        if Player ~= LocalPlayer then
            if not (Config.TeamCheck and Player.Team == LocalPlayer.Team) then
                local Char = Player.Character
                if Char then
                    local Hum = Char:FindFirstChildOfClass("Humanoid")
                    local Part = Char:FindFirstChild(Config.HitPart) or Char:FindFirstChild("HumanoidRootPart")
                    if Hum and Hum.Health > 0 and Part then
                        local ScreenPos, OnScreen = Camera:WorldToViewportPoint(Part.Position)
                        if OnScreen then
                            local Dist = (Vector2.new(ScreenPos.X, ScreenPos.Y) - RefPoint).Magnitude
                            if Dist < ShortestDist and IsVisible(Part, Char) then
                                ShortestDist = Dist
                                ClosestPart = Part
                            end
                        end
                    end
                end
            end
        end
    end

    CachedTarget = ClosestPart
end

-- Loop utama yang ringan
local RenderConn
RenderConn = RunService.RenderStepped:Connect(function()
    if not Config.Enabled then
        FOVCircle.Visible = false
        TargetLine.Visible = false
        CachedTarget = nil
        return
    end

    local RefPoint = GetReferencePoint()
    local Now = os.clock()

    -- Scan target hanya sesuai interval ScanRate (0.05s)
    if (Now - LastScan) >= Config.ScanRate then
        LastScan = Now
        UpdateTargetCache(RefPoint)
    end

    -- Validasi cepat apakah CachedTarget masih hidup
    if CachedTarget and (not CachedTarget.Parent or not CachedTarget.Parent:FindFirstChildOfClass("Humanoid") or CachedTarget.Parent:FindFirstChildOfClass("Humanoid").Health <= 0) then
        CachedTarget = nil
    end

    FOVCircle.Position = RefPoint
    FOVCircle.Radius = Config.FOV
    FOVCircle.Visible = Config.ShowFOV

    if Config.ShowLine and CachedTarget then
        local ScreenPos, OnScreen = Camera:WorldToViewportPoint(CachedTarget.Position)
        if OnScreen then
            TargetLine.From = RefPoint
            TargetLine.To = Vector2.new(ScreenPos.X, ScreenPos.Y)
            TargetLine.Visible = true
        else
            TargetLine.Visible = false
        end
    else
        TargetLine.Visible = false
    end
end)

-- Hook Raycast menggunakan CachedTarget (0 loop saat menembak!)
if not getgenv().OllieHooked then
    getgenv().OllieHooked = true
    local OldNamecall
    OldNamecall = hookmetamethod(game, "__namecall", newcclosure(function(Self, ...)
        local Method = getnamecallmethod()
        local Args = {...}

        if Config.Enabled and CachedTarget and not checkcaller() and Self == Workspace then
            if Method == "Raycast" then
                local Origin = Args[1]
                Args[2] = (CachedTarget.Position - Origin).Unit * 1000
                return OldNamecall(Self, unpack(Args))
            elseif Method == "FindPartOnRay" or Method == "FindPartOnRayWithIgnoreList" then
                local OldRay = Args[1]
                local Origin = OldRay.Origin
                Args[1] = Ray.new(Origin, (CachedTarget.Position - Origin).Unit * 1000)
                return OldNamecall(Self, unpack(Args))
            end
        end

        return OldNamecall(Self, ...)
    end))
end

-- Fungsi cleanup otomatis jika script dijalankan ulang
getgenv().OllieCleanup = function()
    if RenderConn then RenderConn:Disconnect() end
    pcall(function() FOVCircle:Remove() end)
    pcall(function() TargetLine:Remove() end)
    pcall(function() ScreenGui:Destroy() end)
end
