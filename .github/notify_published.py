import re
import requests
import simplejson as json
import sys

is_dev = len(sys.argv) >= 3 and bool(sys.argv[2])
search_string = \
    r'Uploaded to (ossrh|central): (https:\/\/s01\.oss\.sonatype\.org(:443)?\/.*?\/com\/promcteam\/(.*?)\/(.*?)\/(' \
    r'.*?)(?<!sources|javadoc)\.jar(?!\.asc)) '


def get_info():
    with open('log.txt', 'r') as file:
        content = file.read()
        data = re.findall(search_string, content, re.MULTILINE)
        found_version = data[0][5]
        artifact_id = data[0][3]
        artifact_url = data[0][1]
        return found_version, artifact_id, artifact_url


version, name, url = get_info()
if is_dev:
    split = version.split('-')[0:-2]
    version = '-'.join(split)
if not is_dev:
    url = re.sub(
        'https:\/\/s01\.oss\.sonatype\.org:443\/service\/local\/staging\/deployByRepositoryId\/compromcteam-\d+',
        'https://s01.oss.sonatype.org/service/local/repositories/releases/content',
        url)
embed = {
    'username': 'Build Bot',
    'author': {
        'name': 'New ' + ('Dev ' if is_dev else '') + 'Build Available!',
        'url': 'https://github.com/promcteam/' + name
    },
    'image': {
        'url': 'https://promcteam.github.io/proskillapi/' + ('dev_build.gif' if is_dev else 'release_build.gif')
    },
    'title': version,
    'description': 'Click the link above to download the new build!',
    'url': url,
    'color': 5341129
}

requests.post(sys.argv[1],
              headers={'Content-Type': 'application/json'},
              data=json.dumps({'embeds': [embed]})
              )
