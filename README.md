<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>PDF Quiz AI - README</title>

<meta name="viewport" content="width=device-width, initial-scale=1.0">

<!-- Google Font -->
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;800&display=swap" rel="stylesheet">

<!-- Lucide Icons -->
<script src="https://unpkg.com/lucide@latest"></script>

<style>
body {
    margin: 0;
    font-family: 'Inter', sans-serif;
    background: linear-gradient(135deg, #0f172a, #1e293b);
    color: white;
    overflow-x: hidden;
}

.bg-glow {
    position: fixed;
    width: 400px;
    height: 400px;
    background: #4f46e5;
    filter: blur(150px);
    opacity: 0.4;
    animation: move 8s infinite alternate;
    border-radius: 50%;
}

@keyframes move {
    from { transform: translate(-100px, -100px); }
    to { transform: translate(200px, 200px); }
}

.container {
    max-width: 1000px;
    margin: auto;
    padding: 40px;
}

header {
    text-align: center;
    padding: 60px 20px;
}

h1 {
    font-size: 42px;
    background: linear-gradient(90deg, #ff9933, #ffffff, #138808);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
}

.card {
    background: rgba(255,255,255,0.06);
    padding: 20px;
    margin: 20px 0;
    border-radius: 16px;
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255,255,255,0.1);
    transition: 0.3s;
}

.card:hover {
    transform: translateY(-6px);
}

.badge {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 6px 12px;
    margin: 5px;
    border-radius: 999px;
    font-size: 12px;
    background: #4f46e5;
}

.row {
    display: flex;
    align-items: center;
    gap: 10px;
}

pre {
    background: #111827;
    padding: 15px;
    border-radius: 10px;
    overflow-x: auto;
}

footer {
    text-align: center;
    margin-top: 40px;
    opacity: 0.7;
}

.icon {
    width: 18px;
    height: 18px;
}
</style>
</head>

<body>

<div class="bg-glow"></div>

<div class="container">

<header>
    <h1>🎯 PDF-to-QUIZ Generator ⚡</h1>
    <p>AI Powered Full Stack Spring Boot Platform</p>
</header>

<!-- OVERVIEW -->
<div class="card">
    <div class="row">
        <i data-lucide="sparkles" class="icon"></i>
        <h2>Overview</h2>
    </div>

    <p>Convert PDFs into intelligent AI-generated quizzes.</p>

    <span class="badge">
        <i data-lucide="leaf" class="icon"></i> Spring Boot
    </span>

    <span class="badge">
        <i data-lucide="cpu" class="icon"></i> Spring AI
    </span>

    <span class="badge">
        <i data-lucide="layers" class="icon"></i> Full Stack
    </span>
</div>

<!-- FEATURES -->
<div class="card">
    <div class="row">
        <i data-lucide="zap" class="icon"></i>
        <h2>Features</h2>
    </div>

    <ul>
        <li>📄 PDF Upload System</li>
        <li>🤖 AI Quiz Generation</li>
        <li>📊 Score Tracking</li>
        <li>⚡ Fast Processing Engine</li>
    </ul>
</div>

<!-- SETUP -->
<div class="card">
    <div class="row">
        <i data-lucide="settings" class="icon"></i>
        <h2>Setup</h2>
    </div>

<pre>
git clone https://github.com/your-repo
mvn spring-boot:run
</pre>
</div>

<!-- FLOW -->
<div class="card">
    <div class="row">
        <i data-lucide="git-branch" class="icon"></i>
        <h2>System Flow</h2>
    </div>

<pre>
PDF Upload
   ↓
AI Processing
   ↓
Quiz Generation
   ↓
Practice Mode
   ↓
Score Result
</pre>
</div>

<!-- TECH -->
<div class="card">
    <div class="row">
        <i data-lucide="code" class="icon"></i>
        <h2>Tech Stack</h2>
    </div>

<p>
Spring Boot • Spring AI • MySQL • Thymeleaf • HTML/CSS
</p>
</div>

<footer>
    💙 Built with Spring Boot + AI + Passion
</footer>

</div>

<script>
lucide.createIcons();
</script>

</body>
</html>
