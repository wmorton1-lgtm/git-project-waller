import java.io.IOException;

public class Verify {
    public static void main(String[] args) {
        Git repository = new Git();

        try {
            repository.makeGitRepository();
            System.out.println(Git.createTree(""));
            String author = "Taj";
            String message = "Implemented commit";
            String commitHash = Git.createCommit(author, message);
            System.out.println("Commit created successfully");
            System.out.println("Commit hash: " + commitHash);
        } catch (IOException e) {
            System.err.println("Something happened: " + e.getMessage());
        }
    }
}
