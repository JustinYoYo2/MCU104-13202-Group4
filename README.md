<h1 style="color:#5DADE2;">專案檔案管理規則 BY chaoyucheng2024</h1>

<h2 style="color:#48C9B0;">1. 不要亂動專案其他檔案</h2>
<p>除了 <strong>SummonSimulator</strong> 專案檔案，請不要修改或刪除其他檔案。</p>

<h2 style="color:#48C9B0;">2. 請在自己的分支工作</h2>
<p>編輯 SummonSimulator 時，每個人都要建立自己的 <strong>branches</strong>（分支）進行 commit 與 push。</p>

<h2 style="color:#48C9B0;">3. 不要動別人負責的檔案(特別是以下)</h2>
<ul>
  <li>GachaSettings</li>
  <li>SSDBHelper</li>
  <li>MainViewPagerAdapter</li>
</ul>

<h2 style="color:#48C9B0;">4. 資料庫操作統一使用 <strong>GachaSettings</strong></h2>
<p>請不要直接操作 SQLite，要使用提供的方法以確保資料一致。</p>

<h2 style="color:#48C9B0;">5. 圖片檔統一放在 <strong>drawable/</strong></h2>
<p>有問題可私訊管理者。</p>

<h2 style="color:#48C9B0;">6. Push 前一定備份</h2>
<p>每次 push 前先複製專案到自己電腦，避免被別人的修改覆蓋。</p>

<hr>

<h1 style="color:#E67E22;">Git Bash 開發標準流程（SOP）</h1>

<pre>
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
</pre>

<hr>

<h1 style="color:#E67E22;">TortoiseGit 開發標準流程（SOP）</h1>

<h3 style="color:#F5B041;">1. 切換到 main 分支</h3>
<p>右鍵專案資料夾 → TortoiseGit → Switch/Checkout → main → OK</p>

<h3 style="color:#F5B041;">2. 更新 main（Pull）</h3>
<p>右鍵 → TortoiseGit → Pull → origin/main</p>

<h3 style="color:#F5B041;">3. 切換或建立自己的功能分支</h3>
<p>右鍵 → Switch/Checkout → 選分支或建立 <code>feature/你的名字-功能</code></p>

<h3 style="color:#F5B041;">4. 合併 main 到你的分支</h3>
<p>右鍵 → Merge → main → OK</p>

<h3 style="color:#F5B041;">5. 開始寫程式</h3>

<h3 style="color:#F5B041;">6. Commit / Add</h3>

<h3 style="color:#F5B041;">7. Push</h3>

<hr>

<h1 style="color:#BB8FCE;">分支教學</h1>

<h3>建立功能分支</h3>
<pre>
git checkout main
git pull
git checkout -b feature/你的名字-功能
</pre>

<h3>寫完 Commit + Push</h3>
<pre>
git add .
git commit -m "完成首頁按鈕設計"
git push
</pre>

<h3>同步最新 main</h3>
<pre>
git checkout feature/你的分支
git fetch origin
git merge main
</pre>

<h3>最後由管理者合併</h3>
<pre>
git checkout main
git pull
git merge feature/memberB-ui
git merge feature/memberC-ui
git push
</pre>

<hr>

<h1 style="color:#7FB3D5;">GachaSettings & SSDBHelper 使用範例</h1>

<h3>1. 抽卡扣石頭（GachaActivity）</h3>
<pre><code class="language-java">
dbHelper = new SSDBHelper(this);
int current = dbHelper.getStoneCount();
dbHelper.updateStoneCount(-160);
</code></pre>

<h3>2. 儲值增加石頭（TopUpFragment）</h3>
<pre><code class="language-kotlin">
dbHelper = SSDBHelper(context)
val current = dbHelper.getStoneCount()
dbHelper.updateStoneCount(500)
</code></pre>

<h3>3. 使用 GachaSettings 直接抓石頭數量</h3>
<pre><code class="language-java">
GachaSettings settings = GachaSettings.load(this);
int currentStones = settings.getStoneCount();
</code></pre>
