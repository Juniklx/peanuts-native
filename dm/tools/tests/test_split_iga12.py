"""PEANUTS: synthetic IGA v12 archives; no game data. Validates DroidPlay's standalone v12 shader reader before Android Build.
The v13 reader remains separately tested by test_split_iga.py."""
import io
import lzma
import struct
import sys
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[2] / "gfx" / "tools"))
import iga

SECTOR = 0x800
PROPS = dict(id=lzma.FILTER_LZMA1, dict_size=65536, lc=3, lp=0, pb=2)


def fixture(ver=12):
    """One v12 LZMA shader, one stored shader and one LZMA non-shader texture."""
    files = [
        ("build/shaders/Bloom.igz", b"igz" + bytes.fromhex("91688619") + b"x" * 500, True),
        ("build/shaders/Stored.igz", b"stored-shader", False),
        ("build/textures/NoShader.igz", b"unrelated" * 300, True),
    ]
    body = bytearray()
    records = []
    blocks = []
    for name, payload, compressed in files:
        body += bytes((-len(body)) % SECTOR)
        off = len(body)
        if compressed:
            idx = len(blocks)
            props = bytes([93]) + struct.pack("<I", 65536)
            stream = lzma.compress(payload, format=lzma.FORMAT_RAW, filters=[PROPS])
            body += struct.pack("<I", len(stream)) + props + stream
            blocks.append(0x80)  # piece 0 is compressed and begins at sector 0
            blocks.append((len(body) - off + SECTOR - 1) // SECTOR)
            mode = (2 << 28) | idx
        else:
            body += payload
            mode = 0xFFFFFFFF
        records.append((off, len(payload), mode))
    names = bytearray(4 * len(files))
    for i, (name, _, _) in enumerate(files):
        struct.pack_into("<I", names, 4 * i, len(names))
        names += name.encode() + b"\0" + name.split("/")[-1].encode() + b"\0"
    toc = 20 * len(files) + len(blocks)
    data_at = ((0x38 + toc + SECTOR - 1) // SECTOR) * SECTOR
    name_at = data_at + len(body)
    d = bytearray(struct.pack("<4s9IQII", iga.MAGIC, ver, toc, len(files), SECTOR, 1, 0, 0, 0, len(blocks), name_at, len(names), 1))
    d += bytes(4 * len(files))
    for i, (off, length, mode) in enumerate(records):
        d += struct.pack("<4I", data_at + off, 0, length, mode)
    d += bytes(blocks)
    d += bytes(data_at - len(d)) + body + names
    return bytes(d)


class IgaV12(unittest.TestCase):
    def test_shader_folders_only(self):
        result = list(iga.shader_files("fake.pak", opener=lambda *args: io.BytesIO(fixture())))
        self.assertEqual([n for n, _ in result], ["build/shaders/Bloom.igz", "build/shaders/Stored.igz"])
        self.assertEqual(result[0][1], b"igz" + bytes.fromhex("91688619") + b"x" * 500)
        self.assertEqual(result[1][1], b"stored-shader")

    def test_other_iga_version_is_rejected(self):
        with self.assertRaisesRegex(ValueError, "version 13"):
            list(iga.shader_files("fake.pak", opener=lambda *args: io.BytesIO(fixture(ver=13))))

    def test_corrupt_name_bounds_are_rejected(self):
        d = bytearray(fixture())
        # Make the header point the name table outside the bytes
        struct.pack_into("<Q", d, 0x28, len(d) + 0x100)
        with self.assertRaisesRegex(ValueError, "outside the archive"):
            list(iga.shader_files("fake.pak", opener=lambda *args: io.BytesIO(d)))


if __name__ == "__main__":
    unittest.main()
