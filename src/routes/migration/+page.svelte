<div id="migration-container">
    <h1>Editor Migration</h1>
    <p>We are moving the editor to a new home!</p>
    <div class="url-box">
        The new URL is: <a href="https://fabled.travja.dev">fabled.travja.dev</a>
    </div>

    <h2>A Note from Travja</h2>
    <p>
        I've made the decision to transition the editor to a new home to ensure the long-term stability and growth of our entire plugin ecosystem, including <strong>Fabled, Divinity, Codex, and more</strong>.
        The previous domain, <code>magemonkey.studio</code>, is owned by Goflish, who has unfortunately been unavailable since the end of 2025.
        To be proactive and ensure we don't lose access to our tools and documentation, I am establishing a new hub for our suite of plugins at <strong>void.travja.dev</strong>.
    </p>
    <p>
        My goal is to make this transition as smooth as possible for all of you.
        I'm deeply committed to continuing support for these projects and keeping the tools alive and better than ever.
        However, maintaining and evolving a suite like this on my own is a significant undertaking,
        and I truly appreciate your patience and support as I work through the logistics of this move.
    </p>

    <h2>What about my data?</h2>
    <p>
        Your skills and classes are stored in your browser's local storage and <strong>will not</strong> be automatically moved to the new URL.
    </p>
    <p>
        You must manually <strong>backup your data</strong> from this site and <strong>import it</strong> on the new site.
    </p>
    <div class="steps">
        <h3>Steps to Migrate Your Data:</h3>
        <ol>
            <li>On <strong>this</strong> site, click the <span class="material-symbols-rounded inline-icon">cloud_download</span> (Cloud Download) icon in the bottom right of the editor.</li>
            <li>This will download a <code>.yml</code> file containing all your skills and classes.</li>
            <li>Go to the new site: <a href="https://fabled.travja.dev">fabled.travja.dev</a></li>
            <li>On the new site, click the <strong>Import</strong> button in the top navigation bar.</li>
            <li>Select the <code>.yml</code> file you just downloaded.</li>
        </ol>
    </div>

    <h2>Shaping the Future Together</h2>
    <p>
        As I mentioned, I'm currently maintaining these projects primarily as a solo developer.
        If you're a developer who loves these plugins and has experience with web development or Minecraft plugin development, I would love your help!
    </p>
    <p>
        Whether you have an hour a week to squash a bug or want to help implement major new features, your contributions would mean a lot.
        Shaping the future of our plugins is a team effort, and I'd be honored to have more of you involved.
    </p>
    <div class="contribute-link">
        Check out our <a href="https://github.com/magemonkeystudio/fabled">GitHub Repository</a> to get involved!
    </div>
</div>

<style>
    #migration-container {
        max-width: 800px;
        margin: 2rem auto;
        padding: 2rem;
        background-color: #222;
        border-radius: 0.5rem;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.3);
        color: #eee;
        line-height: 1.6;
    }

    .url-box {
        background-color: #333;
        padding: 1rem;
        border-left: 4px solid #0083ef;
        margin: 1.5rem 0;
        font-size: 1.2rem;
        font-weight: bold;
    }

    .steps {
        background-color: #1a1a1a;
        padding: 1rem 1.5rem;
        border-radius: 0.5rem;
        margin-top: 1rem;
    }

    h1 {
        color: #0083ef;
        margin-top: 0;
    }

    h2 {
        color: #1dad36;
        margin-top: 2rem;
    }

    h3 {
        margin-top: 0;
    }

    a {
        color: #ff9800;
        text-decoration: underline;
    }

    a:hover {
        color: #ffa726;
    }

    strong {
        color: #ff5252;
    }

    .inline-icon {
        vertical-align: middle;
        background: #0083ef;
        border-radius: 50%;
        padding: 2px;
        font-size: 1.2rem;
    }

    .contribute-link {
        margin-top: 1rem;
        font-weight: bold;
        text-align: center;
        background-color: rgba(0, 131, 239, 0.1);
        padding: 1rem;
        border-radius: 0.5rem;
        border: 1px solid #0083ef;
    }
</style>
