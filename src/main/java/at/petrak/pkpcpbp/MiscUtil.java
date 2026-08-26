package at.petrak.pkpcpbp;

import org.gradle.api.Project;

import java.util.regex.Pattern;

public class MiscUtil {
  /** Gets a list of commit messages since the previous commit built, prepending {@code - } to the start of each. */
  public static String getRawGitChangelogList(Project project) {
    var gitHash = System.getenv("GIT_COMMIT");
    var gitPrevHash = System.getenv("GIT_PREVIOUS_COMMIT");
    var travisRange = System.getenv("TRAVIS_COMMIT_RANGE");

    if (gitHash != null && gitPrevHash != null) {
      return project.getProviders().exec(spec ->
        spec.commandLine("git", "log", "--pretty=tformat:- %s", gitPrevHash + "..." + gitHash)
      ).getStandardOutput().getAsText().get(); // hack
    } else if (travisRange != null) {
      return project.getProviders().exec(spec ->
        spec.commandLine("git", "log", "--pretty=tformat:- %s", travisRange)
      ).getStandardOutput().getAsText().get();
    } else {
      return "";
    }
  }

  /** Gets the most recent commit message as-is. */
  public static String getMostRecentPush(Project project) {
    return project.getProviders().exec(spec ->
        spec.commandLine("git", "log", "--pretty=tformat:%s", "HEAD~..HEAD")
    ).getStandardOutput().getAsText().get();
  }

  /**
   * Checks if the given commit messages starts with {@code [Release]} (case-insensitive).
   * This should be used with {@link MiscUtil#getMostRecentPush}, <i>not</i> {@link MiscUtil#getRawGitChangelogList}.
   */
  public static boolean isRelease(String changelog) {
    Pattern pat = Pattern.compile("^\\[release", Pattern.CASE_INSENSITIVE);
    return pat.asPredicate().test(changelog);
  }
}
