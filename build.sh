sbt ++2.12.2 "^^1.0.0"   compile publishSigned
sbt ++2.10.6 "^^0.13.16" compile publishSigned
sbt sonatypeList
sbt sonatypeReleaseAll
