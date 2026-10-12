# Security Policy

## Supported versions

Only the latest release of Zero S Mod receives security fixes.

## Reporting a vulnerability

Please do **not** report security issues in public issues, pull requests or Discord.

Report them privately through GitHub's private vulnerability reporting:
<https://github.com/Jingames-Zeros/ZeroSMod/security/advisories/new>
(see [GitHub's guide for reporters](https://docs.github.com/en/code-security/how-tos/report-and-fix-vulnerabilities/report-privately)).
Include what is affected, how to reproduce it, and the impact (for example, what a
player could do to a server).

## Disclosure process

1. We acknowledge your report within 7 days.
2. We confirm the issue, agree on its severity with you, and work on a fix.
3. We release the fix, then publish a security advisory on this repository, crediting
   you if you wish.

Please keep the details private until the advisory is published. If a fix takes longer
than 90 days, we will agree on a disclosure date with you.

## Scope

In scope: the Zero S Mod code in this repository, for example clients being able to
crash or manipulate the server through mod packets, bypass permission checks, or read
or edit server configs they shouldn't.

Out of scope: vulnerabilities in Minecraft, Forge, DBC, CustomNPCs or other mods
(report those to their authors), and issues caused by server misconfiguration.
