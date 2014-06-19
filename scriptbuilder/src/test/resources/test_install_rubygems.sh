if ! hash gem 2>/dev/null; then
(
export TAR_TEMP="$(mktemp -d)"
curl -q -s -S -L --connect-timeout 10 --max-time 600 --retry 20 -X GET  http://production.cf.rubygems.org/rubygems/rubygems-1.8.10.tgz |(mkdir -p "${TAR_TEMP}" &&cd "${TAR_TEMP}" &&tar -xpzf -)
mkdir -p /tmp/rubygems
mv "${TAR_TEMP}"/*/* /tmp/rubygems
rm -rf "${TAR_TEMP}"
cd /tmp/rubygems
ruby setup.rb --no-format-executable
rm -fr /tmp/rubygems
)
fi
gem update --system
gem update --no-rdoc --no-ri
