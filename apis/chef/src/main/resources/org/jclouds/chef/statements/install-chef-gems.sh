if [ ! -f /usr/bin/chef-client ]; then
  apt-get update -o Acquire::http::No-Cache=True
  apt-get install -y ruby ruby1.8-dev build-essential wget libruby-extras libruby1.8-extras
  # Comment next line for production controlled environments, this should be part of a recipe
  apt-get -y upgrade
  (
  mkdir -p /tmp/bootchef
  cd /tmp/bootchef
  wget http://production.cf.rubygems.org/rubygems/rubygems-1.3.7.tgz
  tar zxf rubygems-1.3.7.tgz
  cd rubygems-1.3.7
  ruby setup.rb --no-format-executable
  rm -fr /tmp/bootchef
  )
  /usr/bin/gem install ohai chef --no-rdoc --no-ri --verbose
fi
