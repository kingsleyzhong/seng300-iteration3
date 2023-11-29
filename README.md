# Instructions for Local Machine Replication

1. Create a folder that you want as the root directory of the project. Open a terminal in that folder.
2. Run the command `git clone https://github.com/kingsleyzhong/seng300-iteration3.git`
3. This will create a subfolder called `seng300-project` and inside will be the hardware project, the software project and the software test project.
4. In Eclipse, go to `File > Import` then search for / click on `Existing Projects into Workspace`
5. Select the `seng300-project` folder and check all 3 of the projects. Leave everything else as-is.
6. Everything should already be setup, to test your configuration, go to the test project and run the `SelfCheckoutSystemTest` file, it should pass without errors.
7. Create a new branch called `dev-<what you're doing>`, commit to that branch.

# Collaborating

- Commit to your own branch, each team on their own branch
  - PULL BEFORE YOU START ANY WORK
- If you need the latest changes from main, simply pull from the main branch (using local git, NOT GITHUB). Do this before merging.
- When you are ready to merge to main, open a pull request in GitHub

# Conventions

- Class names are in PascalCase
- Functions / Methods and Variables are in camelCase
- Constants are in MACRO_CASE

# Collaborators

kingsleyzhong, Anthony-kostalvazquez, NDMcCa, suaa-lim, Ekkiddle, CeeRobitaille, dvij99, kngtm1, SubegSC, arcleah, e-woo, Aoi-U, EmilyDStein01, derekatab, s0danissa, enibalo, jli213, estherTran26, Vergeful, Dino-Sour, junheoucal
