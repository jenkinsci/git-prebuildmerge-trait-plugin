package jenkins.plugins.git.traits;

import hudson.Util;
import hudson.model.Descriptor;
import hudson.plugins.git.UserMergeOptions;
import hudson.plugins.git.extensions.GitSCMExtension;
import jenkins.plugins.git.extensions.impl.PreBuildMerge;
import jenkins.model.Jenkins;
import jenkins.plugins.git.GitSCMSource;
import jenkins.scm.api.trait.SCMSourceTrait;
import org.hamcrest.Matchers;
import org.jenkinsci.plugins.gitclient.MergeCommand;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class PreBuildMergeTraitTest {

    @ClassRule
    public static JenkinsRule r = new JenkinsRule();

    @Rule
    public TestName currentTestName = new TestName();

    private GitSCMSource load() {
        return load(currentTestName.getMethodName());
    }

    private GitSCMSource load(String dataSet) {
        return (GitSCMSource) Jenkins.XSTREAM2.fromXML(
                getClass().getResource(getClass().getSimpleName() + "/" + dataSet + ".xml"));
    }

    public List<GitSCMExtensionTraitDescriptor> descriptors() {
        List<GitSCMExtensionTraitDescriptor> list = new ArrayList<>();
        for (Descriptor<SCMSourceTrait> d : SCMSourceTrait.all()) {
            if (d instanceof GitSCMExtensionTraitDescriptor) {
                list.add((GitSCMExtensionTraitDescriptor) d);
            }
        }
        return list;
    }

    @Test
    public void extensionClassesOverrideEquals() {
        for (GitSCMExtensionTraitDescriptor d : descriptors()) {
            assertThat(d.getExtensionClass().getName() + " overrides equals(Object)",
                    Util.isOverridden(GitSCMExtension.class, d.getExtensionClass(), "equals", Object.class),
                    is(true));
        }
    }

    @Test
    public void extensionClassesOverrideHashCode() {
        for (GitSCMExtensionTraitDescriptor d : descriptors()) {
            assertThat(d.getExtensionClass().getName() + " overrides hashCode()",
                    Util.isOverridden(GitSCMExtension.class, d.getExtensionClass(), "hashCode"),
                    is(true));
        }
    }

    @Test
    public void extensionClassesOverrideToString() {
        for (GitSCMExtensionTraitDescriptor d : descriptors()) {
            assertThat(d.getExtensionClass().getName() + " overrides toString()",
                    Util.isOverridden(GitSCMExtension.class, d.getExtensionClass(), "toString"),
                    is(true));
        }
    }

    @Test
    public void prebuildmerge() throws Exception {
        GitSCMSource instance = load();
        assertThat(instance.getId(), is("fd2380f8-d34f-48d5-8006-c34542bc4a89"));
        assertThat(instance.getRemote(), is("git://git.test/example.git"));
        assertThat(instance.getCredentialsId(), is("e4d8c11a-0d24-472f-b86b-4b017c160e9a"));
        assertThat(instance.getTraits(),
                hasItem(
                        Matchers.<SCMSourceTrait>allOf(
                                instanceOf(PreBuildMergeTrait.class),
                                hasProperty("extension",
                                        hasProperty("options",
                                                allOf(
                                                        instanceOf(UserMergeOptions.class),
                                                        hasProperty("mergeRemote", is("foo")),
                                                        hasProperty("mergeTarget", is("bar")),
                                                        hasProperty("mergeStrategy", is(MergeCommand.Strategy.RECURSIVE)),
                                                        hasProperty("fastForwardMode", is(MergeCommand.GitPluginFastForwardMode.NO_FF))
                                                )
                                        )
                                )
                        )
                )
        );
        // Legacy API
        assertThat(instance.getIncludes(), is("foo/*"));
        assertThat(instance.getExcludes(), is("bar/*"));
        assertThat(
                "We have trimmed the extension to only those that are supported on GitSCMSource",
                instance.getExtensions(),
                containsInAnyOrder(
                        Matchers.<GitSCMExtension>allOf(
                                instanceOf(PreBuildMerge.class),
                                hasProperty("options",
                                        allOf(
                                                instanceOf(UserMergeOptions.class),
                                                hasProperty("mergeRemote", is("foo")),
                                                hasProperty("mergeTarget", is("bar")),
                                                hasProperty("mergeStrategy", is(MergeCommand.Strategy.RECURSIVE)),
                                                hasProperty("fastForwardMode", is(MergeCommand.GitPluginFastForwardMode.NO_FF))
                                        )
                                )
                        )
                )
        );
    }
}