sbt ++2.12.8 "^^1.1.6"   compile publishSigned
sbt ++2.10.6 "^^0.13.18" compile publishSigned
sbt sonatypeReleaseAll
