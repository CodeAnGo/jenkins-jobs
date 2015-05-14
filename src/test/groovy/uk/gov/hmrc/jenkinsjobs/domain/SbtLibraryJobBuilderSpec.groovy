package uk.gov.hmrc.jenkinsjobs.domain

import javaposse.jobdsl.dsl.Job
import spock.lang.Specification
import uk.gov.hmrc.jenkinsjobs.JobParents
import uk.gov.hmrc.jenkinsjobs.domain.builder.SbtLibraryJobBuilder

@Mixin(JobParents)
class SbtLibraryJobBuilderSpec extends Specification {

    void 'test XML output'() {
        given:
        SbtLibraryJobBuilder jobBuilder = new SbtLibraryJobBuilder('test-job')

        when:
        Job job = jobBuilder.build(jobParent())

        then:
        with(job.node) {
            scm.userRemoteConfigs.'hudson.plugins.git.UserRemoteConfig'.url.text() == 'git@github.com:hmrc/test-job.git'
            triggers.'com.cloudbees.jenkins.gitHubPushTrigger'.spec.text() == ''
            builders.'hudson.tasks.Shell'.command.text().contains('sbt clean test publishSigned')
            publishers.'hudson.tasks.junit.JUnitResultArchiver'.testResults.text() == 'target/*test-reports/*.xml'
            publishers.'htmlpublisher.HtmlPublisher'.reportTargets.'htmlpublisher.HtmlPublisherTarget'.reportDir[0].text() == 'target/test-reports/html-report'
            publishers.'htmlpublisher.HtmlPublisher'.reportTargets.'htmlpublisher.HtmlPublisherTarget'.reportName[0].text() == 'HTML Report'
        }
    }
}