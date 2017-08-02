package jenkins.plugins.git.extensions.impl;

import hudson.Extension;
import hudson.plugins.git.UserMergeOptions;
import hudson.plugins.git.extensions.GitSCMExtensionDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Speculatively merge the selected commit with another branch before the build to answer the "what happens
 * if I were to integrate this feature branch back to the master?" question.
 */
public class PreBuildMerge extends hudson.plugins.git.extensions.impl.PreBuildMerge {
    private UserMergeOptions options;

    @DataBoundConstructor
    public PreBuildMerge(UserMergeOptions options) {
        super(options);
        this.options = options;
    }

    @Override
    public UserMergeOptions getOptions() {
        return options;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        if (o instanceof PreBuildMerge) {
            PreBuildMerge that = (PreBuildMerge) o;
            return (options != null && options.equals(that.options))
                    || (options == null && that.options == null);
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return PreBuildMerge.class.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "PreBuildMerge{" +
                "options=" + options.toString() +
                '}';
    }

    @Extension
    public static class DescriptorImpl extends GitSCMExtensionDescriptor {
        /**
         * {@inheritDoc}
         */
        @Override
        public String getDisplayName() {
            return "Merge before build";
        }
    }
}
