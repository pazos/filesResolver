# filesResolver

sample activity that shows how to deal with files across android api levels where scoped storage is not enforced.

It showcases two different things:

1. resolving absolute paths on files served by local fileProviders
2. share files with other activities without triggering the FileUriExposedException on api 24+
