<span style="font-size: 32px; font-weight: bold;">專案檔案管理規則 BY chaoyucheng2024</span>
<span style="color: #0055aa; font-size: 22px; font-weight: bold;">1. 不要亂動專案其他檔案</span>

除了 <code>SummonSimulator</code> 專案檔案，請不要修改或刪除其他檔案。

<span style="color: #0055aa; font-size: 22px; font-weight: bold;">2. 請在自己的分支工作</span>

編輯 SummonSimulator 時，每個人都要建立自己的 branches（分支）進行 commit 與 push。

<span style="color: #0055aa; font-size: 22px; font-weight: bold;">3. 不要動別人負責的檔案</span>

特別是：

GachaSettings

SSDBHelper

MainViewPagerAdapter

<span style="color: #0055aa; font-size: 22px; font-weight: bold;">4. 資料庫操作統一使用 GachaSettings</span>

請不要直接操作 SQLite，要使用提供的方法以確保資料一致。

<span style="color: #0055aa; font-size: 22px; font-weight: bold;">5. 圖片檔統一放在 drawable/</span>

有問題可私訊管理者。

<span style="color: #0055aa; font-size: 22px; font-weight: bold;">6. Push 前一定備份</span>

每次 push 前先複製專案到自己電腦，避免被別人的修改覆蓋。

<span style="font-size: 28px; font-weight: bold;">Git Bash 開發標準流程（SOP）</span>
1. git checkout main
2. git pull
3. git checkout 你的分支名稱
   若沒有分支:
   git checkout -b feature/你的名字-功能
4. git merge main
5. 開始寫程式
6. git add .
7. git commit -m "描述修改內容"
8. git push

<span style="font-size: 28px; font-weight: bold;">TortoiseGit 開發標準流程（SOP）</span>
<span style="font-weight: bold; color: #333;">1. 切換到 main 分支</span>

右鍵專案資料夾 → TortoiseGit → Switch/Checkout → 選 main → OK

<span style="font-weight: bold; color: #333;">2. 更新 main（抓遠端最新版本）</span>

右鍵專案資料夾 → TortoiseGit → Pull → origin/main → OK

<span style="font-weight: bold; color: #333;">3. 切換或建立自己的功能分支</span>

右鍵專案 → Switch/Checkout

已有分支：選你的分支 → OK

新建分支：輸入 <code>feature/你的名字-功能</code> → 勾選 Create new branch → OK

<span style="font-weight: bold; color: #333;">4. 合併 main 到你的分支</span>

右鍵 → TortoiseGit → Merge → 選 main → OK
若有衝突 → 使用衝突工具 → 解決 → Commit

<span style="font-weight: bold; color: #333;">5. 開始寫程式</span>

使用 Android Studio 開發功能。

<span style="font-weight: bold; color: #333;">6. Stage / Add 修改</span>

右鍵 → Commit → 勾選修改檔案 → 填寫訊息

<span style="font-weight: bold; color: #333;">7. Commit</span>

點 Commit，完成本地紀錄

<span style="font-weight: bold; color: #333;">8. Push</span>

右鍵 → Push → 選你的分支 → OK

<span style="font-size: 26px; font-weight: bold;">TortoiseGit 流程圖（文字版）</span>
開始
│
▼
切換到 main 分支
│
▼
更新 main（Pull）
│
▼
切換或建立功能分支
│
▼
合併 main（Merge）
│
▼
開始寫程式
│
▼
Commit（Stage/Add）
│
▼
Push 到遠端
│
▼
結束

<span style="font-size: 28px; font-weight: bold;">分支教學</span>
<span style="font-weight: bold;">1. 建立基礎分支</span>

main：穩定版

<span style="font-weight: bold;">2. 開自己的功能分支</span>
git checkout main
git pull
git checkout -b feature/你的名字-功能

<span style="font-weight: bold;">3. 每次寫完要 commit + push</span>
git add .
git commit -m "完成首頁按鈕設計"
git push

<span style="font-weight: bold;">4. 若需要最新 main，定期同步</span>
git checkout feature/你的分支
git fetch origin
git merge main

<span style="font-weight: bold;">5. 最後由管理者統一合併</span>
git checkout main
git pull
git merge feature/memberB-ui
git merge feature/memberC-ui
git push

<span style="font-size: 28px; font-weight: bold;">GachaSettings 與 SSDBHelper 使用範例</span>
1. 抽卡扣石頭（GachaActivity）
dbHelper = new SSDBHelper(this);
int current = dbHelper.getStoneCount();
dbHelper.updateStoneCount(-160);

2. 儲值增加石頭（TopUpFragment）
dbHelper = SSDBHelper(context)
val current = dbHelper.getStoneCount()
dbHelper.updateStoneCount(500)

3. 使用 GachaSettings 直接抓石頭數量
GachaSettings settings = GachaSettings.load(this);
int currentStones = settings.getStoneCount();

