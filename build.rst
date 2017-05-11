build
=====

::

  fly -t concourseci login -c <YOUR CONCOURSE CI URL>
  ACCESS_KEY_ID=<YOUR AWS ACCESS KEY ID> SECRET_ACCESS_KEY=<YOUR SECRET ACCESS KEY> fly -t concourseci execute -c ci/build.yaml -i source=.
