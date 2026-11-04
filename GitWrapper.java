import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;

public class GitWrapper {

    /**
     * Initializes a new Git repository.
     */
    public static void init() {
        Git repository = new Git();

        try {
            repository.makeGitRepository();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize repository", e);
        }
    }

    /**
     * Stages a file for the next commit.
     * Throws IOException if the path does not exist or is a directory.
     * Does nothing if the file's content is already staged for that path.
     *
     * @param filePath The path to the file to stage.
     */
    public static void add(String filePath) throws IOException {
        Path path = Path.of(filePath);

        // no missing things
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            throw new IOException("Not a valid file: " + filePath);
        }

        Path indexPath = Path.of("git", "index");

        if (!Files.isRegularFile(indexPath)) {
            throw new IOException("Repository has not been initialized.");
        }

        String fileHash = Git.hashFile(filePath);
        List<String> indexLines = Files.readAllLines(indexPath);

        for (String line : indexLines) {
            String[] parts = line.split(" ", 2);

            if (parts.length == 2 && parts[1].equals(filePath)
                    && parts[0].equals(fileHash)) {
                return;
            }
        }

        // update index
        Git.updateIndexAndBlob(filePath);
    }

    /**
     * Creates a commit capturing the current state of the index.
     * Builds trees, writes the commit file, updates HEAD.
     *
     * Commit format:
     *   tree: <tree_sha>
     *   parent: <parent_sha>   (omit line for initial commit)
     *   author: <author>
     *   date: <date>
     *   message: <message>
     *
     * @param author  The author name.
     * @param message The commit message.
     * @return The SHA-1 hash of the new commit.
     */
    public static String commit(String author, String message) {
        Path headPath = Path.of("git", "HEAD");
        Path objectsPath = Path.of("git", "objects");

        try {
            if (!Files.isRegularFile(headPath)
                    || !Files.isDirectory(objectsPath)) {
                throw new IOException("Repository has not been initialized.");
            }

            // Build the root tree from the staged index, not the whole

            String treeHash = Git.createRootTree();

            String previousHead = Files.readString(headPath).trim();

            StringBuilder content = new StringBuilder();
            content.append("tree: ").append(treeHash).append("\n");

            // Initial commits do not have a parent
            if (!previousHead.isEmpty()) {
                content.append("parent: ").append(previousHead).append("\n");
            }

            content.append("author: ").append(author).append("\n");
            content.append("date: ").append(LocalDateTime.now()).append("\n");
            content.append("message: ").append(message).append("\n");

            String commitContent = content.toString();

            // hash it up
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = digest.digest(
                    commitContent.getBytes(StandardCharsets.UTF_8));

            String commitHash = HexFormat.of().formatHex(hashBytes);

            // write the commit
            Path commitPath = objectsPath.resolve(commitHash);
            Files.writeString(
                    commitPath,
                    commitContent,
                    StandardCharsets.UTF_8);

            Files.writeString(
                    headPath,
                    commitHash,
                    StandardCharsets.UTF_8);

            return commitHash;

        } catch (IOException e) {
            throw new RuntimeException("Failed to create commit", e);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 is unavailable", e);
        }
    }

    /**
     * EXTRA CREDIT: Restores the working directory to a specific commit.
     *
     * Not implemented yet.
     */
    public static void checkout(String commitHash) {
        throw new UnsupportedOperationException(
                "Checkout has not been implemented yet.");
    }
}