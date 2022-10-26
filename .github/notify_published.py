import requests
import simplejson as json
import re
import sys

def get_info():
    with open('log.txt', 'r') as file:
        content = file.read()
        data = re.findall(
            r'Uploaded to ossrh: (https:\/\/s01\.oss\.sonatype\.org\/.*?\/com\/promcteam\/(.*?)\/(.*?)\/(.*?)\.jar)( .*)?$',
            content, re.MULTILINE)
        version = data[0][3]
        id = data[0][1]
        url = data[0][0]
        return version, id, url


version, name, url = get_info()
embed = {
    'author': {
        'name': 'New Build Available!',
        'url': 'https://github.com/promcteam/' + name
    },
    'title': version,
    'url': url,
    'color': 5341129
}

requests.post(sys.argv[1],
	headers={'Content-Type': 'application/json'},
	data=json.dumps({'embeds': [embed]})
)
