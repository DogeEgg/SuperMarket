pipeline {
    agent any
    stages {
        stage('Build') {
            agent {
                docker {
                    image 'gradle:6.9.0-jdk11'
                    args '-v $HOME/.gradle:/home/gradle/.gradle -v /var/run/docker.sock:/var/run/docker.sock --group-add 992'
                }
            }
            steps {
                script {
                    try {
                        sh 'java -version'
//                        sh 'gradle clean test'
                        runTests(this, supermarket())
                    } catch (err) {
                        throw err
                    }
                }
            }
        }
    }
}

def runTests(pipe, tests) {
    def tags = getTags()
    def success = false
    for (test in tests) {
        stage("Test ${test.name}") {
            verify(test)
            success = true
            echo "Passed"
        }
    }
}

def getTags() {
    def tags = sh script: "git tag --merged", returnStdout: true
    return tags
}

def verify(test) {
    sh "${test.command}"
}

def supermarket() {
    return [
            [
                    name   : 'story1',
                    command: 'rm -r src/test/java; git checkout --ignore-other-worktrees remotes/origin/at -- src/test/acceptance-test src/test/resources/acceptance-test src/test/java/cucumber; ./gradlew clean test -Pat=story1'
            ],
            [
                    name   : 'story2',
                    command: 'rm -r src/test/java; git checkout --ignore-other-worktrees remotes/origin/at -- src/test/acceptance-test src/test/resources/acceptance-test src/test/java/cucumber; ./gradlew clean test -Pat=story2'
            ],
            [
                    name   : 'story3',
                    command: 'rm -r src/test/java; git checkout --ignore-other-worktrees remotes/origin/at -- src/test/acceptance-test src/test/resources/acceptance-test src/test/java/cucumber; ./gradlew clean test -Pat=story3'
            ],
            [
                    name   : 'story4',
                    command: 'rm -r src/test/java; git checkout --ignore-other-worktrees remotes/origin/at -- src/test/acceptance-test src/test/resources/acceptance-test src/test/java/cucumber; ./gradlew clean test -Pat=story4'
            ],
            [
                    name   : 'story5',
                    command: 'rm -r src/test/java; git checkout --ignore-other-worktrees remotes/origin/at -- src/test/acceptance-test src/test/resources/acceptance-test src/test/java/cucumber; ./gradlew clean test -Pat=story5'
            ],
            [
                    name   : 'story6',
                    command: 'rm -r src/test/java; git checkout --ignore-other-worktrees remotes/origin/at -- src/test/acceptance-test src/test/resources/acceptance-test src/test/java/cucumber; ./gradlew clean test -Pat=story6'
            ]
    ]
}
