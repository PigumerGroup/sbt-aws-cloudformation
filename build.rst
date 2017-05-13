build
=====

fly execute::

  fly -t concourseci login -c <YOUR CONCOURSE CI URL>
  AWS_ACCESS_KEY_ID=<YOUR AWS ACCESS KEY ID> AWS_SECRET_ACCESS_KEY=<YOUR SECRET ACCESS KEY> fly -t concourseci execute \
    -c ci/build.yaml \
    -i source=.

fly set-pipeline:

  fly -t concourseci login -c <YOUR CONCOURSE CI URL>
  fly -t concourseci set-pipeline \
    -p sbt-aws-cloudformation \
    -c ci/buildPipeline.yaml \
    -v 'AWS_ACCESS_KEY_ID=<YOUR AWS ACCESS KEY ID>'
    -v 'AWS_SECRET_ACCESS_KEY=<YOUR SECRET KEY>'
