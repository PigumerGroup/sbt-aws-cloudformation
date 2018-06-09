sbt ++2.12.6 "^^1.1.6"   compile publishSigned
sbt ++2.10.6 "^^0.13.16" compile publishSigned
sbt sonatypeReleaseAll
