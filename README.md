makeGitRepository(): guess what it does based on the name. 
 - makes git, objects, index, and HEAD and then prints if they were made successfully or if the directory already exists

hashFile(String filePath): (reused method we made together as a class)
 - takes path as input
 - Reads the file with readAllBytes()
 - turns bytes into hash in byte form with MessageDigest.digest()
 - turns these bytes into hexadecimal and returns
 
createBlob(String filePath):
 - takes path as input
 - copies file into objects with same contents and hash as its name

updateIndex(String filePath):
- takes path as input
- updates index file in git folder
- adds file's hash and path in a line of the index file

updateIndexAndBlob(String filePath):
- takes path as input
- combines createBlob() and updateIndex() into one method call

createWorkingList():
- reads the index and adds blob to the front of each entry
- puts the new entries in an ArrayList<String>
- sorts the entries based on their paths alphabetically

createTree(String dirPath):
- takes path as input
- runs updateIndexAndBlob() on every file in the directory except those inside the git folder and .git and .DS_Store
- returns the root tree hash
- functions as a singular method to be called that runs the functionality of all the code

createTree(ArrayList<String> workingList, String dirPath):
- takes the working list and path as input
- writes the tree for one folder based on its direct children
- returns the hash of that tree

createRootTree():
- builds every tree from the root
- returns the hash of the root tree
- functions as a way to build all the trees with a simple method call

buildTree(ArrayList<String> workingList, String dirPath):
- takes the working list and path as input
- creates the trees for all subfolders within a given directory recursively
- creates the deepest entries first
- returns the hash of the tree built

replaceWithTreeLine(ArrayList<String> workingList, String dirPath, String hash):
- takes the working list and path and the tree hash as input
- replaces the entries in the working list that are inside of the tree with just the tree line

GitWrapper.java:

init():
- Initializes the Git repository by creating the necessary git folder structure
- Uses makeGitRepository() to create the git folder, objects folder, index file, and HEAD file

add(String filePath):
- Takes a file path as input
- Checks that the file exists and is a regular file
- Checks the index to see if the file has already been added with the same content
- If the file has not changed, it does not add it again
- If the file is new or has changed, creates a blob and updates the index

commit(String author, String message):
- Takes the author's name and commit message as input
- Creates the root tree from the files listed in the index
- Reads HEAD to find the previous commit
- Includes the previous commit hash as the parent if one exists
- Creates a commit object containing the tree hash, parent hash, author, date, and message
- Hashes the commit contents using SHA-1
- Writes the commit object into the objects folder using its hash as the filename
- Updates HEAD to point to the new commit
- Returns the new commit hash

checkout(String commitHash):
- Takes a commit hash as input
- Not yet implemented