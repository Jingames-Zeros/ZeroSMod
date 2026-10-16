// Zero S Script API docs: sidebar, "On this page", theme toggle, copy buttons and shortcuts.
// Copied into the Javadoc output and linked from every page by build.gradle.
(function () {
    // This script lives in the docs root, so its own URL gives the root for links.
    // (Javadoc's pathtoroot is defined later in the page than this script runs.)
    var root = document.currentScript.src.replace(/[^/]*$/, "");

    function storageGet(key) { try { return localStorage.getItem(key); } catch (e) { return null; } }
    function storageSet(key, value) { try { localStorage.setItem(key, value); } catch (e) {} }

    // Apply the saved theme before render to avoid a flash
    var saved = storageGet("zs-theme");
    if (saved) document.documentElement.setAttribute("data-theme", saved);

    // Allow line breaks only between camelCase words, like "ProtectedWeapon|Type|Exception"
    function appendWrappable(el, text) {
        text.split(/(?=[A-Z][a-z])/).forEach(function (word, i) {
            if (i > 0) el.appendChild(document.createElement("wbr"));
            el.appendChild(document.createTextNode(word));
        });
    }

    var KIND_BADGE ={ "interface": "I", "class": "C", "exception": "E", "enum": "En", "annotation interface": "@", "record class": "R" };

    // entries: [{u: href from docs root, n: "Outer.Inner", k: kind, p: package}] from sidebar-data.js
    function buildSidebar(entries) {
        var content = document.querySelector(".flex-content");
        if (!content || document.getElementById("zs-sidebar")) return;

        var aside = document.createElement("aside");
        aside.id = "zs-sidebar";
        aside.setAttribute("aria-label", "Classes");

        // On phones the whole sidebar folds into a "Classes" panel
        var panel = document.createElement("details");
        panel.className = "zs-panel";
        panel.open = window.matchMedia("(min-width: 821px)").matches;
        var panelTitle = document.createElement("summary");
        panelTitle.textContent = "Classes";
        panel.appendChild(panelTitle);

        var filter = document.createElement("input");
        filter.type = "search";
        filter.placeholder = "Filter classes…";
        panel.appendChild(filter);

        var byPackage = {};
        entries.forEach(function (t) { (byPackage[t.p] = byPackage[t.p] || []).push(t); });

        var here = location.pathname.replace(/\\/g, "/");
        var collapsed = {};
        try { collapsed = JSON.parse(storageGet("zs-collapsed") || "{}"); } catch (e) {}

        // Zero S packages first, then the bundled CustomNPC+ API under its own heading
        var isCnpc = function (pkg) { return pkg.indexOf("noppes.") === 0; };
        var packages = Object.keys(byPackage).sort(function (a, b) {
            return (isCnpc(a) - isCnpc(b)) || a.localeCompare(b);
        });
        var lastSection = null;

        packages.forEach(function (pkg) {
            var section = isCnpc(pkg) ? "CustomNPC+ API" : "Zero S API";
            if (section !== lastSection && packages.some(isCnpc)) {
                var heading = document.createElement("div");
                heading.className = "zs-section";
                heading.textContent = section;
                panel.appendChild(heading);
                lastSection = section;
            }
            var hasCurrent = byPackage[pkg].some(function (t) { return here.endsWith("/" + t.u); });

            var group = document.createElement("details");
            group.className = "zs-package";
            // CNPC packages start collapsed unless you're on one of their pages
            group.open = pkg in collapsed ? !collapsed[pkg] : (!isCnpc(pkg) || hasCurrent);
            group.addEventListener("toggle", function () {
                collapsed[pkg] = !group.open;
                storageSet("zs-collapsed", JSON.stringify(collapsed));
            });
            var title = document.createElement("summary");
            title.textContent = pkg.replace("org.darkoro.zerosmod.", "").replace("noppes.npcs.", "");
            title.title = pkg;
            group.appendChild(title);

            var ul = document.createElement("ul");
            byPackage[pkg].sort(function (a, b) { return a.n.localeCompare(b.n); }).forEach(function (t) {
                var parts = t.n.split(".");
                var a = document.createElement("a");
                a.href = root + t.u;
                a.title = t.k + " " + t.n;
                var badge = document.createElement("span");
                badge.className = "zs-kind zs-kind-" + t.k.split(" ")[0];
                badge.textContent = KIND_BADGE[t.k] || "?";
                a.appendChild(badge);
                var label = document.createElement("span");
                appendWrappable(label, parts[parts.length - 1]);
                a.appendChild(label);
                if (here.endsWith("/" + t.u)) a.className = "current";
                var li = document.createElement("li");
                li.style.paddingLeft = (parts.length - 1) * 14 + "px"; // nested types sit under their parent
                li.dataset.search = t.n.toLowerCase();
                li.appendChild(a);
                ul.appendChild(li);
            });
            group.appendChild(ul);
            panel.appendChild(group);
        });

        filter.addEventListener("input", function () {
            var q = filter.value.toLowerCase();
            panel.querySelectorAll(".zs-package").forEach(function (group) {
                var any = false;
                group.querySelectorAll("li").forEach(function (li) {
                    var hit = li.dataset.search.indexOf(q) >= 0;
                    li.style.display = hit ? "" : "none";
                    any = any || hit;
                });
                group.style.display = any ? "" : "none";
                if (q) group.open = true;
            });
        });

        aside.appendChild(panel);
        content.insertBefore(aside, content.firstChild);
        var current = aside.querySelector("a.current");
        // Scroll only the sidebar (scrollIntoView would also scroll the page content)
        if (current && panel.open) {
            aside.scrollTop = current.offsetTop - aside.offsetTop - aside.clientHeight / 2;
        }
    }

    function addThemeToggle() {
        var nav = document.querySelector(".top-nav");
        if (!nav) return;
        var btn = document.createElement("button");
        btn.id = "zs-theme-toggle";
        btn.type = "button";
        function label() {
            var light = document.documentElement.getAttribute("data-theme") === "light";
            btn.innerHTML = '<span class="zs-icon">' + (light ? "☾" : "☀") + "</span>" + (light ? "Dark" : "Light");
            btn.title = light ? "Switch to dark mode" : "Switch to light mode";
        }
        btn.addEventListener("click", function () {
            var next = document.documentElement.getAttribute("data-theme") === "light" ? "dark" : "light";
            document.documentElement.setAttribute("data-theme", next);
            storageSet("zs-theme", next);
            label();
        });
        label();
        nav.insertBefore(btn, nav.firstChild);
    }

    function addCopyButtons() {
        document.querySelectorAll(".member-signature").forEach(function (sig) {
            var btn = document.createElement("button");
            btn.type = "button";
            btn.className = "zs-copy";
            btn.textContent = "Copy";
            btn.addEventListener("click", function () {
                var text = sig.textContent.replace(/Copy(ied)?$/, "").replace(/ /g, " ").trim();
                var done = function () {
                    btn.textContent = "Copied";
                    btn.classList.add("done");
                    setTimeout(function () { btn.textContent = "Copy"; btn.classList.remove("done"); }, 1200);
                };
                if (navigator.clipboard) navigator.clipboard.writeText(text).then(done, function () {});
            });
            sig.appendChild(btn);
        });
    }

    // "Method Summary" etc. get a count of their entries
    function addSectionCounts() {
        document.querySelectorAll("section[class$='-summary'] > h2").forEach(function (h2) {
            var rows = h2.parentElement.querySelectorAll(".summary-table > .col-first:not(.table-header), .summary-table > .col-constructor-name:not(.table-header)");
            if (!rows.length) return;
            var count = document.createElement("span");
            count.className = "zs-count";
            count.textContent = rows.length;
            h2.appendChild(count);
        });
    }

    // A "#" link next to each member name, to share a direct link
    function addAnchors() {
        document.querySelectorAll("section[class$='-details'] section.detail[id] > h3").forEach(function (h3) {
            var a = document.createElement("a");
            a.className = "zs-anchor";
            a.href = "#" + h3.parentElement.id;
            a.textContent = "#";
            a.title = "Link to this";
            h3.appendChild(a);
        });
    }

    // "On this page" list of members, highlighting the one being read
    function addToc() {
        var details = document.querySelectorAll("section[class$='-details'] section.detail[id] > h3");
        var content = document.querySelector(".flex-content");
        if (details.length < 3 || !content) return;

        var toc = document.createElement("nav");
        toc.id = "zs-toc";
        toc.setAttribute("aria-label", "On this page");
        var title = document.createElement("div");
        title.className = "zs-toc-title";
        title.textContent = "On this page";
        toc.appendChild(title);

        var seen = {};
        var items = [];
        details.forEach(function (h3) {
            var name = h3.firstChild ? h3.firstChild.textContent.trim() : "";
            var a = document.createElement("a");
            a.href = "#" + h3.parentElement.id;
            seen[name] = (seen[name] || 0) + 1;
            appendWrappable(a, seen[name] > 1 ? name + " (" + seen[name] + ")" : name); // overloads
            toc.appendChild(a);
            items.push({ link: a, target: h3.parentElement });
        });
        content.appendChild(toc);
        document.body.classList.add("zs-has-toc");

        if ("IntersectionObserver" in window) {
            var observer = new IntersectionObserver(function (entries) {
                entries.forEach(function (e) {
                    if (!e.isIntersecting) return;
                    items.forEach(function (it) { it.link.classList.toggle("active", it.target === e.target); });
                });
            }, { root: content, rootMargin: "0px 0px -70% 0px" });
            items.forEach(function (it) { observer.observe(it.target); });
        }
    }

    // Overview page: fill the type counts in the landing section from overview.html
    function fillHeroStats() {
        if (!window.zsSidebar) return;
        var zs = window.zsSidebar.filter(function (t) { return t.p.indexOf("noppes.") !== 0; }).length;
        var counts = { zs: zs, cnpc: window.zsSidebar.length - zs };
        document.querySelectorAll(".zs-hero [data-zs-stat]").forEach(function (el) {
            var n = counts[el.getAttribute("data-zs-stat")];
            if (n) el.querySelector("b").textContent = n;
            else el.remove();
        });
    }

    // "/" focuses search, Esc leaves it
    function addShortcuts() {
        var input = document.getElementById("search-input");
        if (!input) return;
        var hint = document.createElement("span");
        hint.className = "zs-kbd";
        hint.textContent = "/";
        input.parentElement.appendChild(hint);
        input.addEventListener("focus", function () { hint.style.display = "none"; });
        input.addEventListener("blur", function () { if (!input.value) hint.style.display = ""; });
        document.addEventListener("keydown", function (e) {
            var typing = /^(INPUT|TEXTAREA|SELECT)$/.test(document.activeElement.tagName);
            if (e.key === "/" && !typing) { e.preventDefault(); input.focus(); }
            else if (e.key === "Escape" && document.activeElement === input) { input.blur(); }
        });
    }

    function addBackToTop() {
        var content = document.querySelector(".flex-content");
        if (!content) return;
        var btn = document.createElement("button");
        btn.id = "zs-top";
        btn.type = "button";
        btn.title = "Back to top";
        btn.textContent = "↑";
        btn.addEventListener("click", function () { content.scrollTo({ top: 0, behavior: "smooth" }); });
        content.addEventListener("scroll", function () { btn.classList.toggle("show", content.scrollTop > 600); });
        document.body.appendChild(btn);
    }

    // ---------- Java | JS: how each method is called from a CustomNPCs script ----------

    // How scripts reach each type; other types get a variable named after the type
    var SCRIPT_RECEIVERS = {
        "org.darkoro.zerosmod.api.ZSAPI": "ZSAPI",
        "org.darkoro.zerosmod.api.WeaponAPI": "WeaponAPI",
        "org.darkoro.zerosmod.api.ScriptLivingKiMethods": "npc",
        "org.darkoro.zerosmod.api.ScriptPlayerInstantTransmissionMethods": "player",
        "org.darkoro.zerosmod.api.ScriptPlayerTransformationMethods": "player",
        "org.darkoro.zerosmod.api.ScriptKiAttackEntityMethods": "kiAttack",
        "org.darkoro.zerosmod.api.ScriptDBCAddonHeatMethods": "dbcPlayer",
        "org.darkoro.zerosmod.api.ScriptDBCPlayerMethods": "dbcPlayer",
        "org.darkoro.zerosmod.api.ScriptZSWeapon": "weapon",
        "org.darkoro.zerosmod.api.ScriptPlayerCombatState": "state",
        "noppes.npcs.api.AbstractNpcAPI": "API"
    };
    var NUMBER_TYPES = /^(int|long|short|byte|float|double|Integer|Long|Short|Byte|Float|Double|Number)$/;
    var LIST_TYPES = /^(List|ArrayList|Collection|Set|HashSet|Iterable)$/;

    // "IDBCPlayer" -> "dbcPlayer", "IPlayer" -> "player", "ScriptZSWeapon" -> "scriptZSWeapon"
    function variableName(typeName) {
        if (/Event$/.test(typeName)) return "event";
        var name = /^I[A-Z]/.test(typeName) ? typeName.slice(1) : typeName;
        var caps = name.match(/^[A-Z]+(?=[A-Z][a-z]|$)/);
        return caps ? caps[0].toLowerCase() + name.slice(caps[0].length) : name.charAt(0).toLowerCase() + name.slice(1);
    }

    // Split on commas that are not inside generics: "Map<K, V> a, int b" -> ["Map<K, V> a", "int b"]
    function splitTopLevel(text) {
        var parts = [], depth = 0, current = "";
        for (var i = 0; i < text.length; i++) {
            var c = text.charAt(i);
            if (c === "<") depth++;
            if (c === ">") depth--;
            if (c === "," && depth === 0) { parts.push(current); current = ""; } else current += c;
        }
        if (current.trim()) parts.push(current);
        return parts;
    }

    // Java type text -> script (TypeScript-style) type
    function scriptType(javaType) {
        var t = javaType.replace(/@\S+\s*/g, "").trim();
        var array = "";
        while (/(\[\]|\.\.\.)$/.test(t)) { array += "[]"; t = t.replace(/(\[\]|\.\.\.)$/, "").trim(); }
        var generic = t.match(/^([\w.]+)\s*<(.*)>$/);
        var base = (generic ? generic[1] : t).split(".").pop();
        var result;
        if (generic && LIST_TYPES.test(base)) result = scriptType(splitTopLevel(generic[2])[0]) + "[]";
        else if (NUMBER_TYPES.test(base)) result = "number";
        else if (/^(boolean|Boolean)$/.test(base)) result = "boolean";
        else if (/^(String|CharSequence|char|Character)$/.test(base)) result = "string";
        else if (/^(Object|[A-Z])$/.test(base)) result = "any";
        else result = base;
        return result + array;
    }

    // Name for the value a method returns: getPlayerCombatState -> playerCombatState
    function resultName(method) {
        var m = method.match(/^get([A-Z].*)$/);
        if (m) return m[1].charAt(0).toLowerCase() + m[1].slice(1);
        return /^(is|has|was|can|should)[A-Z]/.test(method) ? method : "result";
    }

    function span(cls, text) {
        var s = document.createElement("span");
        s.className = cls;
        s.textContent = text;
        return s;
    }

    // Each method card switches on its own
    function setLang(detail, lang) {
        detail.setAttribute("data-zs-lang", lang);
        detail.querySelectorAll(".zs-lang button").forEach(function (b) {
            b.classList.toggle("on", b.getAttribute("data-lang") === lang);
            b.setAttribute("aria-pressed", b.getAttribute("data-lang") === lang);
        });
    }

    function addScriptViews() {
        var details = document.querySelectorAll("section.method-details section.detail");
        if (!details.length || !window.zsSidebar) return;

        var here = location.pathname.replace(/\\/g, "/");
        var type = window.zsSidebar.filter(function (t) { return here.endsWith("/" + t.u); })[0];
        if (!type) return;
        var simpleName = type.n.split(".").pop();
        var receiver = SCRIPT_RECEIVERS[type.p + "." + type.n] || variableName(simpleName);

        details.forEach(function (detail) {
            var sig = detail.querySelector(".member-signature");
            var nameEl = sig && sig.querySelector(".element-name");
            if (!nameEl) return;
            var method = nameEl.textContent.trim();
            var modifiers = (sig.querySelector(".modifiers") || {}).textContent || "";
            var returnText = ((sig.querySelector(".return-type") || {}).textContent || "void").replace(/ /g, " ");
            var paramsText = ((sig.querySelector(".parameters") || {}).textContent || "()").replace(/ /g, " ");

            var params = splitTopLevel(paramsText.trim().replace(/^\(|\)$/g, "")).map(function (p) {
                var tokens = p.trim().split(/\s+/).filter(function (x) { return x.charAt(0) !== "@"; });
                var name = tokens.pop();
                var javaType = tokens.join(" ");
                return { name: name, type: scriptType(javaType), varargs: /\.\.\.$/.test(javaType) };
            });
            var returns = scriptType(returnText);
            var target = /\bstatic\b/.test(modifiers) && !SCRIPT_RECEIVERS[type.p + "." + type.n] ? simpleName : receiver;

            // Call line: var playerCombatState = ZSAPI.getPlayerCombatState(player);
            var call = document.createElement("code");
            if (returns !== "void") {
                call.appendChild(span("zs-tok-kw", "var "));
                call.appendChild(span("zs-tok-var", resultName(method)));
                call.appendChild(document.createTextNode(" = "));
            }
            call.appendChild(span("zs-tok-recv", target));
            call.appendChild(document.createTextNode("."));
            call.appendChild(span("zs-tok-fn", method));
            call.appendChild(document.createTextNode("("));
            params.forEach(function (p, i) {
                if (i) call.appendChild(document.createTextNode(", "));
                call.appendChild(span("zs-tok-param", (p.varargs ? "..." : "") + p.name));
            });
            call.appendChild(document.createTextNode(");"));

            // Typed signature: getPlayerCombatState(player: IPlayer): ScriptPlayerCombatState
            var typed = document.createElement("code");
            typed.appendChild(span("zs-tok-fn", method));
            typed.appendChild(document.createTextNode("("));
            params.forEach(function (p, i) {
                if (i) typed.appendChild(document.createTextNode(", "));
                typed.appendChild(span("zs-tok-param", (p.varargs ? "..." : "") + p.name));
                typed.appendChild(document.createTextNode(": "));
                typed.appendChild(span("zs-tok-type", p.type));
            });
            typed.appendChild(document.createTextNode("): "));
            typed.appendChild(span("zs-tok-type", returns));

            var view = document.createElement("div");
            view.className = "zs-js";
            var callBox = document.createElement("div");
            callBox.className = "zs-js-call member-signature";
            callBox.appendChild(call);
            view.appendChild(callBox);
            var typedBox = document.createElement("div");
            typedBox.className = "zs-js-typed";
            typedBox.appendChild(span("zs-tok-comment", "// "));
            typedBox.appendChild(typed);
            view.appendChild(typedBox);
            sig.parentNode.insertBefore(view, sig.nextSibling);

            // Java | JS switch in the card header
            var h3 = detail.querySelector("h3");
            if (h3) {
                var toggle = document.createElement("div");
                toggle.className = "zs-lang";
                toggle.setAttribute("role", "group");
                toggle.setAttribute("aria-label", "Signature language");
                ["java", "js"].forEach(function (lang) {
                    var b = document.createElement("button");
                    b.type = "button";
                    b.setAttribute("data-lang", lang);
                    b.textContent = lang === "java" ? "Java" : "JS";
                    b.title = lang === "java" ? "Java signature" : "How to call it from a CustomNPCs script";
                    b.addEventListener("click", function () { setLang(detail, lang); });
                    toggle.appendChild(b);
                });
                h3.appendChild(toggle);
            }
            setLang(detail, "java");
        });
    }

    function init() {
        // By now the page has defined Javadoc's relative pathtoroot; relative links
        // keep Javadoc from marking our own links as external
        if (typeof pathtoroot === "string") root = pathtoroot;
        addThemeToggle();
        addScriptViews(); // before addCopyButtons, so the JS call line gets a Copy button too
        addCopyButtons();
        addSectionCounts();
        addAnchors();
        addToc();
        fillHeroStats();
        addShortcuts();
        addBackToTop();
        // sidebar-data.js is generated at build time and loaded before this script
        if (window.zsSidebar) buildSidebar(window.zsSidebar);
    }

    if (document.readyState === "loading") document.addEventListener("DOMContentLoaded", init);
    else init();
})();
