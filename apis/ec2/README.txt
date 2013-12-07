#
# The jclouds API for Amazon's EC2 service (http://aws.amazon.com/ec2/).
#
# TODO: Implementation status.
# TODO: Supported features.
# TODO: Usage example.

NOTE: The live tests in apis/ec2 will *not* work against AWS EC2 with AWS accounts created
from December 04, 2013 and onward, due to those accounts only supporting VPC, and VPC requiring
different parameters (ID rather than name) for referring to and acting on security groups.

To run the EC2 live tests against AWS, go to providers/aws-ec2.

apis/ec2 will retain the older security group name usage to support EC2 API shims,
such as OpenStack, CloudStack, etc.