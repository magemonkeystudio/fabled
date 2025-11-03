import sys

is_dev = len(sys.argv) >= 2 and bool(sys.argv[1])
remove = '''<repository>
    <id>magemonkey-snapshots</id>
    <url>https://repo.travja.dev/snapshots</url>
</repository>
...
'''

replace = '''<repository>
    <id>magemonkey-releases</id>
    <url>https://repo.travja.dev/releases</url>
</repository>
...
'''


def replace_repository():
    with open('README.md', 'r') as readme:
        contents = readme.read().replace(remove, replace)
    with open('README.md', 'w') as readme:
        readme.write(contents)


replace_repository()
