import re
import sys

is_dev = len(sys.argv) >= 2 and bool(sys.argv[1])
remove = '''<repository>
    <id>sonatype</id>
    <url>https://s01.oss.sonatype.org/content/repositories/snapshots</url>
</repository>
...
'''


def replace_repository():
    with open('README.md', 'r') as readme:
        contents = readme.read().replace(remove, '')
    with open('README.md', 'w') as readme:
        readme.write(contents)


replace_repository()
